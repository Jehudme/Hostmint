import dayjs from 'dayjs/esm';

import { HttpMethod } from 'app/entities/enumerations/http-method.model';
import { IProject } from 'app/entities/project/project.model';
import { IUser } from 'app/entities/user/user.model';

export interface IRequestLog {
  id: number;
  correlationId?: string | null;
  method?: keyof typeof HttpMethod | null;
  path?: string | null;
  statusCode?: number | null;
  durationMs?: number | null;
  principal?: string | null;
  ipAddress?: string | null;
  errorCode?: string | null;
  errorMessage?: string | null;
  createdAt?: dayjs.Dayjs | null;
  actor?: Pick<IUser, 'id' | 'login'> | null;
  project?: Pick<IProject, 'id' | 'projectKey'> | null;
}
