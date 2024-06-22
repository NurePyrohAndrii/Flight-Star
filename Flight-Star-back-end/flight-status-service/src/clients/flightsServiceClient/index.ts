import config from "src/config";

const FLIGHTS_SERVICE_URL = config.flight_service.url;

export const existsByFlightId = async (
  id: string
): Promise<boolean> => {
  const res: Response = await fetch(`${FLIGHTS_SERVICE_URL}/${id}`);
  if (res.status === 200) {
    return true;
  } else if (res.status === 404) {
    return false;
  }
  throw new Error('Failed to check flight existence. Status: ' + res.status);
};