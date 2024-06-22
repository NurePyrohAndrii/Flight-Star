import {Request, Response} from "express";
import {
  createFlightStatus as createFlightStatusApi,
  getFlightStatusCounts as getFlightStatusCountsApi,
  getFlightStatuses as getFlightStatusesApi,
} from "src/flightStatus/flightStatus.service";
import log4js from "log4js";
import {InternalError} from "src/system/internalError";
import httpStatus from "http-status";
import {FlightStatusSaveDto} from "src/flightStatus/dto/FlightStatusSaveDto";

const logger = log4js.getLogger();

export const saveFlightStatus = async (req: Request, res: Response) => {
  try {
    const flightStatus = new FlightStatusSaveDto(req.body);
    const id = await createFlightStatusApi({
      ...flightStatus,
    });
    res.status(httpStatus.CREATED).send({
      id,
    });
  } catch (err) {
    const {message, status} = new InternalError(err);
    logger.error('Error in creating flight status.', err);
    res.status(status).send({message});
  }
};

export const getFlightStatuses = async (req: Request, res: Response) => {
  const {
    flightId,
    size = 10,
    from = 0,
  } = req.query;

  if (!flightId) {
    res.status(httpStatus.BAD_REQUEST).send({message: 'flightId is required'});
  }

  try {
    const flightStatuses = await getFlightStatusesApi(
      flightId as string, Number(size), Number(from)
    );
    if (!flightStatuses) {
      res.status(httpStatus.NOT_FOUND).send();
    } else {
      res.send({
        flightStatuses,
      });
    }
  } catch (err) {
    const {message, status} = new InternalError(err);
    logger.error(`Error in retrieving flight statuses with flightId ${flightId}.`, err);
    res.status(status).send({message});
  }
};

export const getFlightsStatusesCounts = async (req: Request, res: Response) => {
  try {
    const { flightIds } = req.body;

    if (!Array.isArray(flightIds) || flightIds.length === 0) {
      res.status(httpStatus.BAD_REQUEST).send({ message: 'FlightIds must be a non-empty array' });
      return;
    }

    const counts = await getFlightStatusCountsApi(
      flightIds.map(id => id.toString())
    );
    res.status(httpStatus.OK).json(counts);
  } catch (err) {
    const { message, status } = new InternalError(err);
    logger.error('Error in getting flight status counts.', err);
    res.status(status).send({ message });
  }
};