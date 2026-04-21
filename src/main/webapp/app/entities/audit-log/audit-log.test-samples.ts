import dayjs from 'dayjs/esm';

import { IAuditLog } from './audit-log.model';

export const sampleWithRequiredData: IAuditLog = {
  id: 24557,
  action: 'sédentaire crac',
  entityName: 'entre-temps',
  level: 'ERROR',
  message: 'ouille à condition que au-dedans de',
  correlationId: 'vis-à-vie de à seule fin de',
};

export const sampleWithPartialData: IAuditLog = {
  id: 7104,
  action: 'sous couleur de',
  entityName: 'parce que dans la mesure où par rapport à',
  entityId: 'ac4906d7-ea42-4151-9103-8421a19524b5',
  level: 'DEBUG',
  message: 'vivace puisque',
  principal: 'en dehors de apprécier de sorte que',
  correlationId: 'spécialiste pourvoir débile',
  ipAddress: 'à raison de inventer',
  userAgent: 'triathlète présidence',
  createdAt: dayjs('2026-04-21T12:30'),
};

export const sampleWithFullData: IAuditLog = {
  id: 16792,
  action: 'gigantesque lentement malade',
  entityName: 'vide',
  entityId: '1bcf7b0d-7ad2-40ca-b530-e9d91531cda9',
  level: 'WARN',
  message: 'par rédaction considérer',
  principal: 'de manière à ce que pendant que admirer',
  correlationId: 'coin-coin splendide',
  ipAddress: 'admirablement',
  userAgent: 'de façon à',
  metadata: '../fake-data/blob/hipster.txt',
  createdAt: dayjs('2026-04-21T14:14'),
};
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
