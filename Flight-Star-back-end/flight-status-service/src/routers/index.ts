import express from 'express';
import flightStatuses from 'src/flightStatus/flightStatus.router';

const router = express.Router();

router.use('/flight-statuses', flightStatuses);

export default router;
