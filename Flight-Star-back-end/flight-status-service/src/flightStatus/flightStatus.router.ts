import express from 'express';
import {
  getFlightsStatusesCounts,
  getFlightStatuses,
  saveFlightStatus,
} from "src/flightStatus/flightStatus.controller";

const router = express.Router();

router.post('', saveFlightStatus);
router.get('', getFlightStatuses);
router.post('/_counts', getFlightsStatusesCounts);

export default router;