import { afterEach, beforeEach, describe, expect, it } from 'vitest';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';

import { IRequestLog } from '../request-log.model';
import { sampleWithFullData, sampleWithPartialData, sampleWithRequiredData } from '../request-log.test-samples';

import { RequestLogService, RestRequestLog } from './request-log.service';

const requireRestSample: RestRequestLog = {
  ...sampleWithRequiredData,
  createdAt: sampleWithRequiredData.createdAt?.toJSON(),
};

describe('RequestLog Service', () => {
  let service: RequestLogService;
  let httpMock: HttpTestingController;
  let expectedResult: IRequestLog | IRequestLog[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [provideHttpClientTesting()],
    });
    expectedResult = null;
    service = TestBed.inject(RequestLogService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  describe('Service methods', () => {
    it('should find an element', () => {
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.find(123).subscribe(resp => (expectedResult = resp));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of RequestLog', () => {
      const returnedFromService = { ...requireRestSample };

      const expected = { ...sampleWithRequiredData };

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toMatchObject([expected]);
    });

    it('should handle exceptions for searching a RequestLog', () => {
      const queryObject: any = {
        page: 0,
        size: 20,
        query: '',
        sort: [],
      };
      service.search(queryObject).subscribe(() => expectedResult);

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush(null, { status: 500, statusText: 'Internal Server Error' });
      expect(expectedResult).toBe(null);
    });

    describe('addRequestLogToCollectionIfMissing', () => {
      it('should add a RequestLog to an empty array', () => {
        const requestLog: IRequestLog = sampleWithRequiredData;
        expectedResult = service.addRequestLogToCollectionIfMissing([], requestLog);
        expect(expectedResult).toEqual([requestLog]);
      });

      it('should not add a RequestLog to an array that contains it', () => {
        const requestLog: IRequestLog = sampleWithRequiredData;
        const requestLogCollection: IRequestLog[] = [
          {
            ...requestLog,
          },
          sampleWithPartialData,
        ];
        expectedResult = service.addRequestLogToCollectionIfMissing(requestLogCollection, requestLog);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a RequestLog to an array that doesn't contain it", () => {
        const requestLog: IRequestLog = sampleWithRequiredData;
        const requestLogCollection: IRequestLog[] = [sampleWithPartialData];
        expectedResult = service.addRequestLogToCollectionIfMissing(requestLogCollection, requestLog);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(requestLog);
      });

      it('should add only unique RequestLog to an array', () => {
        const requestLogArray: IRequestLog[] = [sampleWithRequiredData, sampleWithPartialData, sampleWithFullData];
        const requestLogCollection: IRequestLog[] = [sampleWithRequiredData];
        expectedResult = service.addRequestLogToCollectionIfMissing(requestLogCollection, ...requestLogArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const requestLog: IRequestLog = sampleWithRequiredData;
        const requestLog2: IRequestLog = sampleWithPartialData;
        expectedResult = service.addRequestLogToCollectionIfMissing([], requestLog, requestLog2);
        expect(expectedResult).toEqual([requestLog, requestLog2]);
      });

      it('should accept null and undefined values', () => {
        const requestLog: IRequestLog = sampleWithRequiredData;
        expectedResult = service.addRequestLogToCollectionIfMissing([], null, requestLog, undefined);
        expect(expectedResult).toEqual([requestLog]);
      });

      it('should return initial array if no RequestLog is added', () => {
        const requestLogCollection: IRequestLog[] = [sampleWithRequiredData];
        expectedResult = service.addRequestLogToCollectionIfMissing(requestLogCollection, undefined, null);
        expect(expectedResult).toEqual(requestLogCollection);
      });
    });

    describe('compareRequestLog', () => {
      it('should return true if both entities are null', () => {
        const entity1 = null;
        const entity2 = null;

        const compareResult = service.compareRequestLog(entity1, entity2);

        expect(compareResult).toEqual(true);
      });

      it('should return false if one entity is null', () => {
        const entity1 = { id: 28326 };
        const entity2 = null;

        const compareResult1 = service.compareRequestLog(entity1, entity2);
        const compareResult2 = service.compareRequestLog(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('should return false if primaryKey differs', () => {
        const entity1 = { id: 28326 };
        const entity2 = { id: 30287 };

        const compareResult1 = service.compareRequestLog(entity1, entity2);
        const compareResult2 = service.compareRequestLog(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('should return false if primaryKey matches', () => {
        const entity1 = { id: 28326 };
        const entity2 = { id: 28326 };

        const compareResult1 = service.compareRequestLog(entity1, entity2);
        const compareResult2 = service.compareRequestLog(entity2, entity1);

        expect(compareResult1).toEqual(true);
        expect(compareResult2).toEqual(true);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
