import { Routes } from '@angular/router';

import { ASC } from 'app/config/navigation.constants';
import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';

import RequestLogResolve from './route/request-log-routing-resolve.service';

const requestLogRoute: Routes = [
  {
    path: '',
    loadComponent: () => import('./list/request-log').then(m => m.RequestLog),
    data: {
      defaultSort: `id,${ASC}`,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    loadComponent: () => import('./detail/request-log-detail').then(m => m.RequestLogDetail),
    resolve: {
      requestLog: RequestLogResolve,
    },
    canActivate: [UserRouteAccessService],
  },
];

export default requestLogRoute;
