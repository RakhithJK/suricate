/*
 *
 *  * Copyright 2012-2018 the original author or authors.
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

import io.suricate.monitoring.model.dto.api.error.ApiErrorDto;
import io.suricate.monitoring.model.dto.api.project.ProjectRequestDto;
import io.suricate.monitoring.model.dto.api.projectgrid.ProjectGridRequestDto;
import io.suricate.monitoring.model.entities.Project;
import io.suricate.monitoring.model.entities.ProjectGrid;
import io.suricate.monitoring.model.enums.ApiErrorEnum;
import io.suricate.monitoring.services.api.ProjectGridService;
import io.suricate.monitoring.services.api.ProjectService;
import io.suricate.monitoring.utils.exceptions.ApiException;
import io.suricate.monitoring.utils.exceptions.ObjectNotFoundException;
import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static io.suricate.monitoring.utils.exceptions.constants.ErrorMessage.*;

/**
 * Project Grid controller
 */
@RestController
@RequestMapping("/api")
@Api(value = "Project Grid Controller", tags = {"Project Grids"})
public class ProjectGridController {
    /**
     * Project service
     */
    private final ProjectService projectService;

    /**
     * Project grid service
     */
    private final ProjectGridService projectGridService;

    /**
     * Constructor for dependency injection
     *
     * @param projectService The project service
     * @param projectGridService The project grid service
     */
    @Autowired
    public ProjectGridController(final ProjectService projectService,
                                 final ProjectGridService projectGridService) {
        this.projectService = projectService;
        this.projectGridService = projectGridService;
    }

    /**
     * Update an existing project
     *
     * @param authentication    The connected user
     * @param projectToken      The project token to update
     * @param projectRequestDtos The project grids information to update
     * @return The project updated
     */
    @ApiOperation(value = "Update an existing project by the project token")
    @ApiResponses(value = {
            @ApiResponse(code = 204, message = "Project updated"),
            @ApiResponse(code = 401, message = "Authentication error, token expired or invalid", response = ApiErrorDto.class),
            @ApiResponse(code = 403, message = "You don't have permission to access to this resource", response = ApiErrorDto.class),
            @ApiResponse(code = 404, message = "Project not found", response = ApiErrorDto.class)
    })
    @PutMapping(value = "/v1/projectGrids/{projectToken}")
    @PreAuthorize("hasRole('ROLE_USER')")
    public ResponseEntity<Void> updateProjectGrids(@ApiIgnore OAuth2Authentication authentication,
                                              @ApiParam(name = "projectToken", value = "The project token", required = true)
                                              @PathVariable("projectToken") String projectToken,
                                              @ApiParam(name = "projectResponseDto", value = "The project information", required = true)
                                              @RequestBody List<ProjectGridRequestDto> projectRequestDtos) {
        Optional<Project> projectOptional = projectService.getOneByToken(projectToken);
        if (!projectOptional.isPresent()) {
            throw new ObjectNotFoundException(Project.class, projectToken);
        }

        Project project = projectOptional.get();
        if (!this.projectService.isConnectedUserCanAccessToProject(project, authentication.getUserAuthentication())) {
            throw new ApiException(USER_NOT_ALLOWED_PROJECT, ApiErrorEnum.NOT_AUTHORIZED);
        }

        List<Long> gridIds = projectRequestDtos
                .stream()
                .map(ProjectGridRequestDto::getId)
                .collect(Collectors.toList());
        if (project.getGrids().stream().noneMatch(grid -> gridIds.contains(grid.getId()))) {
            throw new ApiException(USER_NOT_ALLOWED_GRID, ApiErrorEnum.NOT_AUTHORIZED);
        }

        projectGridService.updateAll(project.getGrids(), projectRequestDtos);

        return ResponseEntity.noContent().build();
    }

    /**
     * Delete a given grid of a project
     *
     * @param authentication The connected user
     * @param projectToken   The project token to delete
     * @param gridId         The grid id
     * @return A void response entity
     */
    @ApiOperation(value = "Delete a grid by the grid id")
    @ApiResponses(value = {
            @ApiResponse(code = 204, message = "Project deleted"),
            @ApiResponse(code = 401, message = "Authentication error, token expired or invalid", response = ApiErrorDto.class),
            @ApiResponse(code = 403, message = "You don't have permission to access to this resource", response = ApiErrorDto.class),
            @ApiResponse(code = 404, message = "Project not found", response = ApiErrorDto.class)
    })
    @DeleteMapping(value = "/v1/projectGrids/{projectToken}/{gridId}")
    @PreAuthorize("hasRole('ROLE_USER')")
    public ResponseEntity<Void> deleteGridById(@ApiIgnore OAuth2Authentication authentication,
                                               @ApiParam(name = "projectToken", value = "The project token", required = true)
                                               @PathVariable("projectToken") String projectToken,
                                               @ApiParam(name = "gridId", value = "The grid id", required = true)
                                               @PathVariable("gridId") Long gridId) {
        Optional<Project> projectOptional = projectService.getOneByToken(projectToken);

        if (!projectOptional.isPresent()) {
            throw new ObjectNotFoundException(Project.class, projectToken);
        }

        Project project = projectOptional.get();
        if (!projectService.isConnectedUserCanAccessToProject(project, authentication.getUserAuthentication())) {
            throw new ApiException(USER_NOT_ALLOWED_PROJECT, ApiErrorEnum.NOT_AUTHORIZED);
        }

        if (project.getGrids().stream().noneMatch(grid -> grid.getId().equals(gridId))) {
            throw new ApiException(USER_NOT_ALLOWED_GRID, ApiErrorEnum.NOT_AUTHORIZED);
        }

        projectGridService.deleteByProjectIdAndId(project.getId(), gridId);

        return ResponseEntity.noContent().build();
    }
}
