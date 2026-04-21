import dayjs from 'dayjs/esm';

import { LogLevel } from 'app/entities/enumerations/log-level.model';
import { IProject } from 'app/entities/project/project.model';
import { IUser } from 'app/entities/user/user.model';

export interface IAuditLog {
  id: number;
  action?: string | null;
  entityName?: string | null;
  entityId?: string | null;
  level?: keyof typeof LogLevel | null;
  message?: string | null;
  principal?: string | null;
  correlationId?: string | null;
  ipAddress?: string | null;
  userAgent?: string | null;
  metadata?: string | null;
  createdAt?: dayjs.Dayjs | null;
  actor?: Pick<IUser, 'id' | 'login'> | null;
  project?: Pick<IProject, 'id' | 'projectKey'> | null;
}
