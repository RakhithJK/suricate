/*
 * Copyright 2012-2018 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import { Component, Inject, OnInit } from '@angular/core';
import { MAT_DIALOG_DATA } from '@angular/material/dialog';
import { FormGroup } from '@angular/forms';

import { Project } from '../../../../../shared/models/backend/project/project';
import { WebsocketClient } from '../../../../../shared/models/backend/websocket-client';
import { HttpScreenService } from '../../../../../shared/services/backend/http-screen.service';
import { HttpProjectService } from '../../../../../shared/services/backend/http-project.service';
import { FormField } from '../../../../../shared/models/frontend/form/form-field';
import { FormService } from '../../../../../shared/services/frontend/form.service';
import { TranslateService } from '@ngx-translate/core';
import { map } from 'rxjs/operators';
import { DataTypeEnum } from '../../../../../shared/enums/data-type.enum';
import { CustomValidators } from 'ng2-validation';
import { Observable } from 'rxjs';

/**
 * Component that manage the popup for Dashboard TV Management
 */
@Component({
  selector: 'suricate-tv-management-dialog',
  templateUrl: './tv-management-dialog.component.html',
  styleUrls: ['./tv-management-dialog.component.scss']
})
export class TvManagementDialogComponent implements OnInit {
  /**
   * The register screen form
   * @type {FormGroup}
   */
  screenRegisterForm: FormGroup;

  /**
   * The description of the form
   */
  formFields: FormField[];

  /**
   * The current project
   * @type {Project}
   */
  project: Project;

  /**
   * The list of clients connected by websocket
   * @type {WebsocketClient[]}
   */
  websocketClients: WebsocketClient[];

  /**
   * Constructor
   *
   * @param data The data give to the modal
   * @param {FormService} formService The formService
   * @param {HttpProjectService} httpProjectService The http project service to inject
   * @param {HttpScreenService} httpScreenService The screen service
   * @param {TranslateService} translateService The translate service
   */
  constructor(
    @Inject(MAT_DIALOG_DATA) private data: any,
    private formService: FormService,
    private httpProjectService: HttpProjectService,
    private httpScreenService: HttpScreenService,
    private translateService: TranslateService
  ) {}

  /**
   * When the component is initialized
   */
  ngOnInit() {
    this.httpProjectService.getById(this.data.projectToken).subscribe(project => {
      this.project = project;
      this.refreshWebsocketClients();
    });

    this.generateFormFields().subscribe(() => {
      this.screenRegisterForm = this.formService.generateFormGroupForFields(this.formFields);
    });
  }

  generateFormFields(): Observable<void> {
    this.formFields = [];
    return this.translateService.get(['screen.field.code']).pipe(
      map((translations: string) => {
        this.formFields.push({
          key: 'screenCode',
          label: translations['screen.field.code'],
          type: DataTypeEnum.NUMBER,
          value: '',
          validators: [CustomValidators.digits, CustomValidators.gt(0)]
        });
      })
    );
  }

  refreshWebsocketClients() {
    this.httpProjectService.getProjectWebsocketClients(this.project.token).subscribe(websocketClients => {
      this.websocketClients = websocketClients;
    });
  }

  /**
   * Register the screen
   */
  registerScreen(): void {
    if (this.screenRegisterForm.valid) {
      const screenCode: string = this.screenRegisterForm.get('screenCode').value;

      this.httpScreenService.connectProjectToScreen(this.project.token, +screenCode).subscribe(() => {
        this.screenRegisterForm.reset();
        setTimeout(() => this.refreshWebsocketClients(), 2000);
      });
    }
  }

  /**
   * Disconnect a websocket
   *
   * @param {WebsocketClient} websocketClient The websocket to disconnect
   */
  disconnectScreen(websocketClient: WebsocketClient): void {
    this.httpScreenService.disconnectScreen(websocketClient.projectToken, +websocketClient.screenCode).subscribe(() => {
      setTimeout(() => this.refreshWebsocketClients(), 2000);
    });
  }

  /**
   * Display the screen code on every connected screens
   * @param projectToken The project token
   */
  displayScreenCode(projectToken: string): void {
    if (projectToken) {
      this.httpScreenService.displayScreenCodeEveryConnectedScreensForProject(projectToken).subscribe();
    }
  }
}
