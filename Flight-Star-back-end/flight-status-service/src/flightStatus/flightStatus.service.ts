import FlightStatus, {IFlightStatus, Status} from "src/flightStatus/flightStatus.model";
import {FlightStatusSaveDto} from "src/flightStatus/dto/FlightStatusSaveDto";
import {existsByFlightId} from "src/clients/flightsServiceClient";
import {FlightStatusInfoDto} from "src/flightStatus/dto/FlightStatusInfoDto";
import {sendEmailMessage} from "src/clients/kafkaClient";

/**
 * Create flight status with given flight status dto
 *
 * @param flightStatusDto - flight status dto to create
 */
export const createFlightStatus = async (
  flightStatusDto: FlightStatusSaveDto
): Promise<string> => {
  await validateFlightStatus(flightStatusDto);
  const flightStatus = await new FlightStatus(flightStatusDto).save();

  // Construct the Kafka message
  const kafkaMessage = {
    subject: 'Flight Status Created',
    text: `Flight status with id ${flightStatus._id} has been created.
    Flight id: ${flightStatus.flightId}.\n
    Status: ${flightStatus.status}.\n
    Timestamp: ${flightStatus.timestamp}.`,
    recipient: '',
  };

  // Send the message to the Kafka topic
  await sendEmailMessage('mail', kafkaMessage);

  return flightStatus._id;
};

/**
 * Get flight statuses by flight id
 *
 * @param flightId - flight id to get statuses
 * @param size - number of statuses to get
 * @param from - start index to get statuses
 */
export const getFlightStatuses = async (
  flightId: string,
  size: number,
  from: number
): Promise<FlightStatusInfoDto[] | null> => {
  const flightStatuses = await FlightStatus.find({flightId})
    .sort({timestamp: -1})
    .skip(from)
    .limit(size);

  return flightStatuses
    ? flightStatuses.map(
      status => toFlightStatusInfoDto(status))
    : null;
};

/**
 * Get flight status counts by flight ids
 *
 * @param flightIds - flight ids array to get statuses counts
 */
export const getFlightStatusCounts = async (
  flightIds: Array<string>
): Promise<Record<string, number>> => {
  const counts = await FlightStatus.aggregate([
    {$match: {flightId: {$in: flightIds.map(id => id.toString())}}},
    {$group: {_id: '$flightId', count: {$sum: 1}}},
  ]);

  const result = counts.reduce((acc, item) => {
    acc[item._id] = item.count;
    return acc;
  }, {} as Record<string, number>);

  flightIds.forEach(id => {
    if (!result[id]) {
      result[id] = 0;
    }
  });

  return result;
};

/**
 * Convert flight status to flight status info dto
 *
 * @param flightStatus - flight status to convert
 */
const toFlightStatusInfoDto = (
  flightStatus: IFlightStatus
): FlightStatusInfoDto => {
  return {
    _id: flightStatus._id,
    flightId: flightStatus.flightId,
    status: flightStatus.status,
    timestamp: flightStatus.timestamp,
  };
};

/**
 * Validate flight status
 *
 * @param flightStatusDto - flight status dto to validate
 */
export const validateFlightStatus = async (
  flightStatusDto: FlightStatusSaveDto
) => {
  const id = flightStatusDto.flightId;

  if (!id) {
    throw new Error('Flight id is required.');
  }

  if (!Object.values(Status).includes(flightStatusDto.status as Status)) {
    throw new Error('Status is invalid.');
  }

  const isExistByFlightId = await existsByFlightId(id);
  if (!isExistByFlightId) {
    throw new Error(`Flight with id ${id} does not exist.`);
  }
};
