/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.screens.library.client.screens.project;

import javax.annotation.PostConstruct;
import javax.enterprise.event.Event;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import com.google.gwt.core.client.Callback;
import elemental2.dom.HTMLElement;
import elemental2.promise.Promise;
import org.guvnor.common.services.project.context.WorkspaceProjectContextChangeEvent;
import org.guvnor.common.services.project.model.WorkspaceProject;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.common.client.dom.elemental2.Elemental2DomUtil;
import org.jboss.errai.ioc.client.api.ManagedInstance;
import org.kie.workbench.common.screens.defaulteditor.client.editor.NewFileUploader;
import org.kie.workbench.common.screens.library.api.LibraryService;
import org.kie.workbench.common.screens.library.client.perspective.LibraryPerspective;
import org.kie.workbench.common.screens.library.client.screens.assets.AssetsScreen;
import org.kie.workbench.common.screens.library.client.screens.assets.events.UpdatedAssetsEvent;
import org.kie.workbench.common.screens.library.client.screens.organizationalunit.contributors.tab.ContributorsListPresenter;
import org.kie.workbench.common.screens.library.client.screens.organizationalunit.contributors.tab.ProjectContributorsListServiceImpl;
import org.kie.workbench.common.screens.library.client.screens.project.actions.ProjectMainActions;
import org.kie.workbench.common.screens.library.client.screens.project.branch.delete.DeleteBranchPopUpScreen;
import org.kie.workbench.common.screens.library.client.screens.project.delete.DeleteProjectPopUpScreen;
import org.kie.workbench.common.screens.library.client.screens.project.rename.RenameProjectPopUpScreen;
import org.kie.workbench.common.screens.library.client.settings.SettingsPresenter;
import org.kie.workbench.common.screens.library.client.util.LibraryPermissions;
import org.kie.workbench.common.screens.library.client.util.LibraryPlaces;
import org.kie.workbench.common.screens.projecteditor.client.validation.ProjectNameValidator;
import org.kie.workbench.common.screens.projecteditor.service.ProjectScreenService;
import org.kie.workbench.common.widgets.client.handlers.NewResourcePresenter;
import org.kie.workbench.common.widgets.client.handlers.NewResourceSuccessEvent;
import org.uberfire.backend.vfs.Path;
import org.uberfire.client.annotations.WorkbenchPartTitle;
import org.uberfire.client.annotations.WorkbenchPartView;
import org.uberfire.client.annotations.WorkbenchScreen;
import org.uberfire.client.mvp.UberElemental;
import org.uberfire.client.promise.Promises;
import org.uberfire.ext.editor.commons.client.file.CommandWithFileNameAndCommitMessage;
import org.uberfire.ext.editor.commons.client.file.popups.CopyPopUpPresenter;
import org.uberfire.lifecycle.OnMayClose;
import org.uberfire.workbench.events.NotificationEvent;

@WorkbenchScreen(identifier = LibraryPlaces.PROJECT_SCREEN,
        owningPerspective = LibraryPerspective.class)
public class ProjectScreen {

    private Elemental2DomUtil elemental2DomUtil;
    protected WorkspaceProject workspaceProject;

    public interface View extends UberElemental<ProjectScreen> {

        void setAssetsCount(int count);

        void setContributorsCount(int count);

        void setContent(HTMLElement content);

        void setTitle(String projectName);

        void setAddAssetVisible(boolean visible);

        void setImportAssetVisible(boolean visible);

        void setDuplicateVisible(boolean visible);

        void setReimportVisible(boolean visible);

        void setDeleteProjectVisible(boolean visible);

        void setDeleteBranchVisible(boolean visible);

        void setActionsVisible(boolean visible);

        String getLoadingMessage();

        String getItemSuccessfullyDuplicatedMessage();

        String getReimportSuccessfulMessage();

        void showBusyIndicator(String loadingMessage);

        void hideBusyIndicator();

        void addMainAction(HTMLElement element);
    }

    private final LibraryPlaces libraryPlaces;

    private AssetsScreen assetsScreen;
    private ContributorsListPresenter contributorsListScreen;
    private ProjectMetricsScreen projectMetricsScreen;
    private LibraryPermissions libraryPermissions;
    private SettingsPresenter settingsPresenter;
    private final NewFileUploader newFileUploader;
    private final NewResourcePresenter newResourcePresenter;
    private ManagedInstance<DeleteProjectPopUpScreen> deleteProjectPopUpScreen;
    private ManagedInstance<DeleteBranchPopUpScreen> deleteBranchPopUpScreen;
    private ManagedInstance<RenameProjectPopUpScreen> renameProjectPopUpScreen;
    private Caller<LibraryService> libraryService;
    private ProjectScreen.View view;
    private Caller<ProjectScreenService> projectScreenService;
    private CopyPopUpPresenter copyPopUpPresenter;
    private ProjectNameValidator projectNameValidator;
    private Promises promises;
    private Event<NotificationEvent> notificationEvent;
    private ProjectContributorsListServiceImpl projectContributorsListService;
    private ProjectMainActions projectMainActions;

    @Inject
    public ProjectScreen(final View view,
                         final LibraryPlaces libraryPlaces,
                         final AssetsScreen assetsScreen,
                         final ContributorsListPresenter contributorsListScreen,
                         final ProjectMetricsScreen projectMetricsScreen,
                         final LibraryPermissions libraryPermissions,
                         final SettingsPresenter settingsPresenter,
                         final NewFileUploader newFileUploader,
                         final NewResourcePresenter newResourcePresenter,
                         final ManagedInstance<DeleteProjectPopUpScreen> deleteProjectPopUpScreen,
                         final ManagedInstance<DeleteBranchPopUpScreen> deleteBranchPopUpScreen,
                         final ManagedInstance<RenameProjectPopUpScreen> renameProjectPopUpScreen,
                         final Caller<LibraryService> libraryService,
                         final Caller<ProjectScreenService> projectScreenService,
                         final CopyPopUpPresenter copyPopUpPresenter,
                         final ProjectNameValidator projectNameValidator,
                         final Promises promises,
                         final Event<NotificationEvent> notificationEvent,
                         final ProjectContributorsListServiceImpl projectContributorsListService,
                         final ProjectMainActions projectMainActions) {
        this.view = view;
        this.libraryPlaces = libraryPlaces;
        this.assetsScreen = assetsScreen;
        this.contributorsListScreen = contributorsListScreen;
        this.projectMetricsScreen = projectMetricsScreen;
        this.libraryPermissions = libraryPermissions;
        this.settingsPresenter = settingsPresenter;
        this.newFileUploader = newFileUploader;
        this.newResourcePresenter = newResourcePresenter;
        this.deleteProjectPopUpScreen = deleteProjectPopUpScreen;
        this.deleteBranchPopUpScreen = deleteBranchPopUpScreen;
        this.renameProjectPopUpScreen = renameProjectPopUpScreen;
        this.libraryService = libraryService;
        this.projectScreenService = projectScreenService;
        this.copyPopUpPresenter = copyPopUpPresenter;
        this.projectNameValidator = projectNameValidator;
        this.promises = promises;
        this.notificationEvent = notificationEvent;
        this.projectContributorsListService = projectContributorsListService;
        this.elemental2DomUtil = new Elemental2DomUtil();
        this.projectMainActions = projectMainActions;
    }

    @PostConstruct
    public void initialize() {
        this.workspaceProject = this.libraryPlaces.getActiveWorkspace();
        this.view.init(this);
        this.view.setTitle(libraryPlaces.getActiveWorkspace().getName());
        this.view.addMainAction(projectMainActions.getElement());
        this.resolveAssetsCount();
        this.showAssets();

        final boolean userCanUpdateProject = this.userCanUpdateProject();
        final boolean userCanDeleteProject = this.userCanDeleteProject();
        final boolean userCanDeleteBranch = this.userCanDeleteBranch();
        final boolean userCanBuildProject = this.userCanBuildProject();
        final boolean userCanDeployProject = this.userCanDeployProject();
        final boolean userCanCreateProjects = this.userCanCreateProjects();

        this.view.setAddAssetVisible(userCanUpdateProject);
        this.view.setImportAssetVisible(userCanUpdateProject);
        this.view.setDuplicateVisible(userCanCreateProjects);
        this.view.setReimportVisible(userCanUpdateProject);
        this.view.setDeleteProjectVisible(userCanDeleteProject);
        this.view.setDeleteBranchVisible(userCanDeleteBranch);

        setupMainActions();

        this.view.setActionsVisible(userCanUpdateProject || userCanDeleteProject || userCanBuildProject || userCanDeployProject || userCanCreateProjects);

        newFileUploader.acceptContext(new Callback<Boolean, Void>() {
            @Override
            public void onFailure(Void reason) {
                view.setImportAssetVisible(false);
            }

            @Override
            public void onSuccess(Boolean result) {
                view.setImportAssetVisible(result && userCanUpdateProject);
            }
        });

        contributorsListScreen.setup(projectContributorsListService,
                                     contributorCount -> this.view.setContributorsCount(contributorCount));
    }

    private void setupMainActions() {
        projectMainActions.setBuildEnabled(userCanBuildProject());
        projectMainActions.setDeployEnabled(userCanDeployProject());
        projectMainActions.setRedeployEnabled(workspaceProject.getMainModule().getPom().getGav().isSnapshot());
    }

    @OnMayClose
    public boolean onMayClose() {
        return settingsPresenter.mayClose();
    }

    public void setAssetsCount(Integer assetsCount) {
        this.view.setAssetsCount(assetsCount);
    }

    public void onAddAsset(@Observes NewResourceSuccessEvent event) {
        resolveAssetsCount();
    }

    public void onAssetsUpdated(@Observes UpdatedAssetsEvent event) {
        resolveAssetsCount();
    }

    public void changeProjectAndTitleWhenContextChange(@Observes final WorkspaceProjectContextChangeEvent current) {
        if (current.getWorkspaceProject() != null) {
            this.workspaceProject = current.getWorkspaceProject();
            this.view.setTitle(workspaceProject.getName());
            setupMainActions();
        }
    }

    private void resolveAssetsCount() {
        this.libraryService.call((Integer numberOfAssets) -> this.setAssetsCount(numberOfAssets))
                .getNumberOfAssets(this.workspaceProject);
    }

    public void showAssets() {
        this.view.setContent(this.assetsScreen.getView().getElement());
    }

    public void showMetrics() {
        this.projectMetricsScreen.onStartup(workspaceProject);
        this.view.setContent(elemental2DomUtil.asHTMLElement(this.projectMetricsScreen.getView().getElement()));
    }

    public void showContributors() {
        this.view.setContent(elemental2DomUtil.asHTMLElement(this.contributorsListScreen.getView().getElement()));
    }

    @WorkbenchPartTitle
    public String getTitle() {
        return this.libraryPlaces.getActiveWorkspace().getName();
    }

    public void deleteProject() {
        if (userCanDeleteProject()) {
            final DeleteProjectPopUpScreen popUp = deleteProjectPopUpScreen.get();
            popUp.show(this.workspaceProject);
        }
    }

    public void deleteBranch() {
        if (userCanDeleteBranch()) {
            final DeleteBranchPopUpScreen popUp = deleteBranchPopUpScreen.get();
            popUp.show(this.libraryPlaces.getActiveWorkspace().getBranch());
        }
    }

    public void addAsset() {
        if (userCanUpdateProject()) {
            this.libraryPlaces.goToAddAsset();
        }
    }

    public void importAsset() {
        if (userCanUpdateProject()) {
            newFileUploader.getCommand(newResourcePresenter).execute();
        }
    }

    public void showSettings() {
        settingsPresenter.setupUsingCurrentSection().then(i -> {
            SettingsPresenter.View settingsView = this.settingsPresenter.getView();
            this.view.setContent(settingsView.getElement());
            return promises.resolve();
        });
    }

    public void rename() {
        if (userCanUpdateProject()) {
            final RenameProjectPopUpScreen popUp = renameProjectPopUpScreen.get();
            popUp.show(this.workspaceProject);
        }
    }

    public void duplicate() {
        if (this.userCanCreateProjects()) {
            copyPopUpPresenter.show(
                    workspaceProject.getRootPath(),
                    projectNameValidator,
                    getDuplicateCommand());
        }
    }

    CommandWithFileNameAndCommitMessage getDuplicateCommand() {
        return details -> {
            copyPopUpPresenter.getView().hide();

            view.showBusyIndicator(view.getLoadingMessage());

            promises.promisify(projectScreenService, s -> {
                s.copy(workspaceProject, details.getNewFileName());
            }).then(i -> {
                view.hideBusyIndicator();
                notificationEvent.fire(new NotificationEvent(view.getItemSuccessfullyDuplicatedMessage(),
                                                             NotificationEvent.NotificationType.SUCCESS));
                return promises.resolve();
            }).catch_(this::onError);
        };
    }

    public void reimport() {
        if (this.userCanUpdateProject()) {
            final Path pomXMLPath = workspaceProject.getMainModule().getPomXMLPath();
            view.showBusyIndicator(view.getLoadingMessage());

            promises.promisify(projectScreenService, s -> {
                s.reImport(pomXMLPath);
            }).then(i -> {
                view.hideBusyIndicator();
                notificationEvent.fire(new NotificationEvent(view.getReimportSuccessfulMessage(),
                                                             NotificationEvent.NotificationType.SUCCESS));
                return promises.resolve();
            }).catch_(this::onError);
        }
    }

    private Promise<Object> onError(final Object o) {
        return promises.catchOrExecute(o, e -> {
            view.hideBusyIndicator();
            return promises.reject(e);
        }, x -> {
            view.hideBusyIndicator();
            return promises.reject(x);
        });
    }

    public boolean userCanDeleteProject() {
        return libraryPermissions.userCanDeleteProject(this.workspaceProject);
    }

    public boolean userCanDeleteBranch() {
        return userCanDeleteProject() && !workspaceProject.getBranch().getName().equals("master");
    }

    public boolean userCanBuildProject() {
        return libraryPermissions.userCanBuildProject(this.workspaceProject);
    }

    public boolean userCanDeployProject() {
        return libraryPermissions.userCanDeployProject(this.workspaceProject);
    }

    public boolean userCanUpdateProject() {
        return libraryPermissions.userCanUpdateProject(this.workspaceProject);
    }

    public boolean userCanCreateProjects() {
        return libraryPermissions.userCanCreateProject(libraryPlaces.getActiveSpace());
    }

    @WorkbenchPartView
    public ProjectScreen.View getView() {
        return view;
    }
}
