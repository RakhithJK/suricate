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

import { TestBed } from '@angular/core/testing';

import { MenuService } from './menu.service';
import { MockModule } from '../../../../mock/mock.module';
import { FormService } from '../form/form.service';

describe('MenuService', () => {
  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [MockModule],
      providers: [MenuService]
    });
  });

  it('should create', () => {
    const service: MenuService = TestBed.inject(MenuService);
    expect(service).toBeTruthy();
  });
});
