import {Status} from "src/flightStatus/flightStatus.model";

export class FlightStatusSaveDto {
  flightId?: string;
  status?: Status;

  constructor(data: Partial<FlightStatusSaveDto>) {
    this.flightId = data.flightId;
    this.status = data.status;
  }
}