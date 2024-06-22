import chai from 'chai';
import sinon from 'sinon';
import mongoSetup from "./mongoSetup";
import chaiHttp from "chai-http";
import express from "express";
import router from "src/flightStatus/flightStatus.router";
import bodyParser from 'body-parser';
import * as flightServiceClient from "src/clients/flightsServiceClient";
import FlightStatus from "src/flightStatus/flightStatus.model";

const {expect} = chai;
chai.use(chaiHttp);
chai.should();

const sandbox = sinon.createSandbox();

const app = express();

app.use(bodyParser.json({limit: '1mb'}));
app.use('', router);

describe('FlightStatusController', () => {
  before(async () => {
    await mongoSetup;
  });

  afterEach(() => {
    sandbox.restore();
  });

  it('should save flight status', (done) => {
    // Given
    const clientCallStub = sandbox.stub(flightServiceClient, 'existsByFlightId');
    clientCallStub.resolves(true);
    // When
    chai.request(app)
      .post('')
      .send({
        flightId: '11',
        status: 'DELAYED',
      })
    // Then
      .end((_, res) => {
        res.should.have.status(201);
        expect(res.body).to.have.property('id');
        expect(res.body.id).to.be.not.empty;
        done();
      });
  });

  it('should return 400 if flightId is not provided', (done) => {
    // When
    chai.request(app)
      .post('')
      .send({
        status: 'DELAYED',
      })
    // Then
      .end((_, res) => {
        res.should.have.status(500);
        expect(res.body).to.have.property('message').eq('Flight id is required.');
        done();
      });
  });

  it('should return 400 if status is not provided', (done) => {
    // When
    chai.request(app)
      .post('')
      .send({
        flightId: '11',
      })
    // Then
      .end((_, res) => {
        res.should.have.status(500);
        expect(res.body).to.have.property('message').eq('Status is invalid.');
        done();
      });
  });

  it('should return saved flight status', async () => {
    // Given
    const flightStatusDto = {
      flightId: '100',
      status: 'DELAYED',
    };
    const flightStatus = await new FlightStatus(flightStatusDto).save();
    // When
    chai.request(app)
      .get('/?flightId=' + flightStatusDto.flightId)
    // Then
      .end((_, res) => {
        res.should.have.status(200);
        expect(res.body.flightStatuses[0]).to.have.property('flightId').eq(flightStatus.flightId);
        expect(res.body.flightStatuses[0]).to.have.property('status').eq('DELAYED');
      });
  });

  it('should return 400 if flightId is not provided', (done) => {
    // When
    chai.request(app)
      .get('/')
    // Then
      .end((_, res) => {
        res.should.have.status(400);
        expect(res.body).to.have.property('message').eq('flightId is required');
        done();
      });
  });

  it('should return flight status counts', async () => {

    // Given
    const flightStatusDto = {
      flightId: '101',
      status: 'DELAYED',
    };
    const flightStatus = await new FlightStatus(flightStatusDto).save();

    // When
    chai.request(app)
      .post('/_counts')
      .send({
        flightIds: [flightStatus.flightId, '102'],
      })
    // Then
      .end((_, res) => {
        res.should.have.status(200);
        expect(res.body).to.have.property('101').eq(1);
        expect(res.body).to.have.property('102').eq(0);
      });
  });

  it('should return 400 if flightIds is not provided', (done) => {
    // When
    chai.request(app)
      .post('/_counts')
      .send({})
    // Then
      .end((_, res) => {
        res.should.have.status(400);
        expect(res.body).to.have.property('message').eq('FlightIds must be a non-empty array');
        done();
      });
  });
});