/*
 *
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

package io.suricate.monitoring.controllers;

import io.suricate.monitoring.model.dto.api.setting.SettingResponseDto;
import io.suricate.monitoring.model.entities.Setting;
import io.suricate.monitoring.services.api.SettingService;
import io.suricate.monitoring.services.mapper.SettingMapper;
import io.suricate.monitoring.utils.exceptions.NoContentException;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;

/**
 * Settings controller
 */
@RestController
@RequestMapping("/api")
@Api(value = "Setting Controller", tags = {"Settings"})
public class SettingController {

    /**
     * Settings service
     */
    private final SettingService settingService;

    /**
     * Settings mapper
     */
    private final SettingMapper settingMapper;

    /**
     * Constructor
     *
     * @param settingService The setting service
     * @param settingMapper  The setting mapper
     */
    @Autowired
    public SettingController(final SettingService settingService,
                             final SettingMapper settingMapper) {
        this.settingService = settingService;
        this.settingMapper = settingMapper;
    }

    /**
     * Get the full list of settings
     *
     * @return The full list of settings
     */
    @ApiOperation(value = "Get the full list of settings", response = SettingResponseDto.class, nickname = "getAllSettings")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "Ok", response = SettingResponseDto.class, responseContainer = "List"),
        @ApiResponse(code = 204, message = "No Content")
    })
    @GetMapping(value = "/v1/settings")
    public ResponseEntity<List<SettingResponseDto>> getAll() {
        Optional<List<Setting>> settingsOptional = settingService.getAll();

        if (!settingsOptional.isPresent()) {
            throw new NoContentException(Setting.class);
        }

        return ResponseEntity
            .ok()
            .contentType(MediaType.APPLICATION_JSON)
            .body(settingMapper.toSettingsDTOs(settingsOptional.get()));
    }
}
