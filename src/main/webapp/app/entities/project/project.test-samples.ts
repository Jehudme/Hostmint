import dayjs from 'dayjs/esm';

import { IProject, NewProject } from './project.model';

export const sampleWithRequiredData: IProject = {
  id: 22823,
  name: 'comment',
  projectKey: 'BHY64',
};

export const sampleWithPartialData: IProject = {
  id: 17773,
  name: 'quitte à chef tandis que',
  projectKey: '_V',
  updatedAt: dayjs('2026-04-21T05:42'),
};

export const sampleWithFullData: IProject = {
  id: 1375,
  name: 'rectorat parer membre titulaire',
  projectKey: 'IA',
  createdAt: dayjs('2026-04-21T16:21'),
  updatedAt: dayjs('2026-04-21T06:26'),
};

export const sampleWithNewData: NewProject = {
  name: 'toc quant à',
  projectKey: 'VOS1',
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
