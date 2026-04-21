import { entityDetailsBackButtonSelector, entityDetailsButtonSelector, entityTableSelector } from '../../support/entity';

describe('RequestLog e2e test', () => {
  const requestLogPageUrl = '/request-log';
  const requestLogPageUrlPattern = new RegExp('/request-log(\\?.*)?$');
  let username: string;
  let password: string;

  let requestLog;

  before(() => {
    cy.credentials().then(credentials => {
      ({ username, password } = credentials);
    });
  });

  beforeEach(() => {
    cy.login(username, password);
  });

  beforeEach(() => {
    cy.intercept('GET', '/api/request-logs+(?*|)').as('entitiesRequest');
    cy.intercept('POST', '/api/request-logs').as('postEntityRequest');
    cy.intercept('DELETE', '/api/request-logs/*').as('deleteEntityRequest');
  });

  afterEach(() => {
    if (requestLog) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/api/request-logs/${requestLog.id}`,
      }).then(() => {
        requestLog = undefined;
      });
    }
  });

  it('RequestLogs menu should load RequestLogs page', () => {
    cy.visit('/');
    cy.clickOnEntityMenuItem('request-log');
    cy.wait('@entitiesRequest').then(({ response }) => {
      if (response?.body.length === 0) {
        cy.get(entityTableSelector).should('not.exist');
      } else {
        cy.get(entityTableSelector).should('exist');
      }
    });
    cy.getEntityHeading('RequestLog').should('exist');
    cy.url().should('match', requestLogPageUrlPattern);
  });

  describe('RequestLog page', () => {
    describe('with existing value', () => {
      beforeEach(function () {
        cy.visit(requestLogPageUrl);

        cy.wait('@entitiesRequest').then(({ response }) => {
          if (response?.body.length === 0) {
            this.skip();
          }
        });
      });

      it('detail button click should load details RequestLog page', () => {
        cy.get(entityDetailsButtonSelector).first().click();
        cy.getEntityDetailsHeading('requestLog');
        cy.get(entityDetailsBackButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', requestLogPageUrlPattern);
      });
    });
  });
});
