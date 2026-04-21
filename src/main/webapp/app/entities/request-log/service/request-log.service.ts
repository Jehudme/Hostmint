import { HttpClient, HttpResponse, httpResource } from '@angular/common/http';
import { Injectable, computed, inject, signal } from '@angular/core';

import dayjs from 'dayjs/esm';
import { Observable, asapScheduler, map, scheduled } from 'rxjs';
import { catchError } from 'rxjs/operators';

import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { SearchWithPagination } from 'app/core/request/request.model';
import { isPresent } from 'app/core/util/operators';
import { IRequestLog } from '../request-log.model';

type RestOf<T extends IRequestLog> = Omit<T, 'createdAt'> & {
  createdAt?: string | null;
};

export type RestRequestLog = RestOf<IRequestLog>;

@Injectable()
export class RequestLogsService {
  readonly requestLogsParams = signal<Record<string, string | number | boolean | readonly (string | number | boolean)[]> | undefined>(
    undefined,
  );
  readonly requestLogsResource = httpResource<RestRequestLog[]>(() => {
    const params = this.requestLogsParams();
    if (!params) {
      return undefined;
    }
    return { url: params.query ? this.resourceSearchUrl : this.resourceUrl, params };
  });
  /**
   * This signal holds the list of requestLog that have been fetched. It is updated when the requestLogsResource emits a new value.
   * In case of error while fetching the requestLogs, the signal is set to an empty array.
   */
  readonly requestLogs = computed(() =>
    (this.requestLogsResource.hasValue() ? this.requestLogsResource.value() : []).map(item => this.convertValueFromServer(item)),
  );
  protected readonly applicationConfigService = inject(ApplicationConfigService);
  protected readonly resourceUrl = this.applicationConfigService.getEndpointFor('api/request-logs');
  protected readonly resourceSearchUrl = this.applicationConfigService.getEndpointFor('api/request-logs/_search');

  protected convertValueFromServer(restRequestLog: RestRequestLog): IRequestLog {
    return {
      ...restRequestLog,
      createdAt: restRequestLog.createdAt ? dayjs(restRequestLog.createdAt) : undefined,
    };
  }
}

@Injectable({ providedIn: 'root' })
export class RequestLogService extends RequestLogsService {
  protected readonly http = inject(HttpClient);

  find(id: number): Observable<IRequestLog> {
    return this.http
      .get<RestRequestLog>(`${this.resourceUrl}/${encodeURIComponent(id)}`)
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  query(req?: any): Observable<HttpResponse<IRequestLog[]>> {
    const options = createRequestOption(req);
    return this.http
      .get<RestRequestLog[]>(this.resourceUrl, { params: options, observe: 'response' })
      .pipe(map(res => res.clone({ body: this.convertResponseArrayFromServer(res.body!) })));
  }

  search(req: SearchWithPagination): Observable<IRequestLog[]> {
    const options = createRequestOption(req);
    return this.http.get<RestRequestLog[]>(this.resourceSearchUrl, { params: options }).pipe(
      map(res => this.convertResponseArrayFromServer(res)),
      catchError(() => scheduled([], asapScheduler)),
    );
  }

  getRequestLogIdentifier(requestLog: Pick<IRequestLog, 'id'>): number {
    return requestLog.id;
  }

  compareRequestLog(o1: Pick<IRequestLog, 'id'> | null, o2: Pick<IRequestLog, 'id'> | null): boolean {
    return o1 && o2 ? this.getRequestLogIdentifier(o1) === this.getRequestLogIdentifier(o2) : o1 === o2;
  }

  addRequestLogToCollectionIfMissing<Type extends Pick<IRequestLog, 'id'>>(
    requestLogCollection: Type[],
    ...requestLogsToCheck: (Type | null | undefined)[]
  ): Type[] {
    const requestLogs: Type[] = requestLogsToCheck.filter(isPresent);
    if (requestLogs.length > 0) {
      const requestLogCollectionIdentifiers = requestLogCollection.map(requestLogItem => this.getRequestLogIdentifier(requestLogItem));
      const requestLogsToAdd = requestLogs.filter(requestLogItem => {
        const requestLogIdentifier = this.getRequestLogIdentifier(requestLogItem);
        if (requestLogCollectionIdentifiers.includes(requestLogIdentifier)) {
          return false;
        }
        requestLogCollectionIdentifiers.push(requestLogIdentifier);
        return true;
      });
      return [...requestLogsToAdd, ...requestLogCollection];
    }
    return requestLogCollection;
  }

  protected convertValueFromClient<T extends IRequestLog>(requestLog: T): RestOf<T> {
    return {
      ...requestLog,
      createdAt: requestLog.createdAt?.toJSON() ?? null,
    };
  }

  protected convertResponseFromServer(res: RestRequestLog): IRequestLog {
    return this.convertValueFromServer(res);
  }

  protected convertResponseArrayFromServer(res: RestRequestLog[]): IRequestLog[] {
    return res.map(item => this.convertValueFromServer(item));
  }
}
