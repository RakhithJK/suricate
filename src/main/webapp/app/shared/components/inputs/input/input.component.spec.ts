/*
 *  /*
 *  * Copyright 2012-2021 the original author or authors.
 *  *
 *  * Licensed under the Apache License, Version 2.0 (the "License");
 *  * you may not use this file except in compliance with the License.
 *  * You may obtain a copy of the License at
 *  *
 *  *      http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing, software
 *  * distributed under the License is distributed on an "AS IS" BASIS,
 *  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  * See the License for the specific language governing permissions and
 *  * limitations under the License.
 *
 */

import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';

import { InputComponent } from './input.component';
import { MockedModelBuilderService } from '../../../../mock/services/mocked-model-builder/mocked-model-builder.service';
import { DataTypeEnum } from '../../../enums/data-type.enum';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { TranslateLoader, TranslateModule } from '@ngx-translate/core';
import { HttpClient } from '@angular/common/http';
import { TranslateHttpLoader } from '@ngx-translate/http-loader';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { MatAutocompleteModule } from '@angular/material/autocomplete';
import { MockModule } from '../../../../mock/mock.module';

describe('InputComponent', () => {
  let component: InputComponent;
  let fixture: ComponentFixture<InputComponent>;

  beforeEach(
    waitForAsync(() => {
      TestBed.configureTestingModule({
        imports: [MockModule],
        declarations: [InputComponent]
      }).compileComponents();

      const mockedModelBuilderService = TestBed.inject(MockedModelBuilderService);

      fixture = TestBed.createComponent(InputComponent);
      component = fixture.componentInstance;
      component.field = mockedModelBuilderService.buildMockedFormField(DataTypeEnum.TEXT);
      component.formGroup = mockedModelBuilderService.buildMockedFormGroup(DataTypeEnum.TEXT);

      fixture.detectChanges();
    })
  );

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
