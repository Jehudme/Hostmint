import dayjs from 'dayjs/esm';

import { IRequestLog } from './request-log.model';

export const sampleWithRequiredData: IRequestLog = {
  id: 572,
  correlationId: 'mairie vu que malade',
  method: 'HEAD',
  path: 'inspecter loufoque moquer',
  statusCode: 442,
  durationMs: 7375,
};

export const sampleWithPartialData: IRequestLog = {
  id: 23539,
  correlationId: 'bè incorporer',
  method: 'HEAD',
  path: 'pschitt fade',
  statusCode: 486,
  durationMs: 25444,
  ipAddress: 'expédier féliciter',
  createdAt: dayjs('2026-04-21T03:12'),
};

export const sampleWithFullData: IRequestLog = {
  id: 28106,
  correlationId: 'pacifique psitt pendant que',
  method: 'PATCH',
  path: 'longtemps',
  statusCode: 418,
  durationMs: 19367,
  principal: 'tromper près dans',
  ipAddress: 'discuter',
  errorCode: 'dès que alors que',
  errorMessage: 'aïe',
  createdAt: dayjs('2026-04-21T16:45'),
};
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
