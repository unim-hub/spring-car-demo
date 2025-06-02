import React from 'react';

export default function VehicleApp({ carDemoConfig }) {
  return (
    <div>
      <p>vehicle_service: {carDemoConfig.vehicle_service}</p>
      <p>vehicle_ws: {carDemoConfig.vehicle_ws}</p>
    </div>
  );
}
