import { Routes } from '@angular/router';

const routes: Routes = [
  {
    path: 'authority',
    data: { pageTitle: 'hostMintApp.adminAuthority.home.title' },
    loadChildren: () => import('./admin/authority/authority.routes'),
  },
  {
    path: 'project',
    data: { pageTitle: 'hostMintApp.project.home.title' },
    loadChildren: () => import('./project/project.routes'),
  },
  {
    path: 'audit-log',
    data: { pageTitle: 'hostMintApp.auditLog.home.title' },
    loadChildren: () => import('./audit-log/audit-log.routes'),
  },
  {
    path: 'request-log',
    data: { pageTitle: 'hostMintApp.requestLog.home.title' },
    loadChildren: () => import('./request-log/request-log.routes'),
  },
  {
    path: 'user-management',
    data: { pageTitle: 'userManagement.home.title' },
    loadChildren: () => import('./admin/user-management/user-management.routes'),
  },
  /* jhipster-needle-add-entity-route - JHipster will add entity modules routes here */
];

export default routes;
