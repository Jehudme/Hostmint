import dayjs from 'dayjs/esm';

import { IUser } from 'app/entities/user/user.model';

export interface IProject {
  id: number;
  name?: string | null;
  projectKey?: string | null;
  createdAt?: dayjs.Dayjs | null;
  updatedAt?: dayjs.Dayjs | null;
  owner?: Pick<IUser, 'id' | 'login'> | null;
}

export type NewProject = Omit<IProject, 'id'> & { id: null };
