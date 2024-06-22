import mongoose, { Schema, Document } from 'mongoose';

export enum Status {
  DEPARTED = 'DEPARTED',
  ARRIVED = 'ARRIVED',
  DELAYED = 'DELAYED',
  CANCELLED = 'CANCELLED',
}

export interface IFlightStatus extends Document {
  flightId: string;
  status: Status;
  timestamp: Date;
}

const FlightStatusSchema: Schema = new Schema({
  flightId: {
    type: String,
    required: true,
  },
  status: {
    type: String,
    enum: Status,
    required: true,
  },
  timestamp: {
    type: Date,
    required: true,
    default: Date.now,
  },
});

const FlightStatus = mongoose.model<IFlightStatus>('FlightStatus', FlightStatusSchema);

export default FlightStatus;