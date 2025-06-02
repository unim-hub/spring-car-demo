// File: WebSocketViewer.jsx

import React, { useEffect, useState } from 'react';

function VehicleApp ({ carDemoConfig }) {
  const [speed, setSpeed] = useState([]);
  const [connectionStatus, setConnectionStatus] = useState('Connecting...');

  useEffect(() => {
    const socket = new WebSocket(carDemoConfig.vehicle_ws);

    socket.onopen = () => {
      setConnectionStatus('Connected');
      console.log('WebSocket connected');
    };

    socket.onmessage = (event) => {
      try {
        const data = JSON.parse(event.data);
        switch(data.eventType) {
          case 1: {
            setSpeed(data.speed);
            break;
          }
          default:
            console.log("ERROR: Invalid eventId. Supported {speed:1}\n event:" + data);
        };
      } catch (err) {
        console.error('Invalid JSON received:', event.data);
      }
    };

    socket.onerror = (error) => {
      console.error('WebSocket error:', error);
      setConnectionStatus('Error');
    };

    socket.onclose = () => {
      setConnectionStatus('Closed');
    };

    // Cleanup on unmount
    return () => {
      socket.close();
    };
  }, [carDemoConfig.vehicle_ws]);

  return (
    <div>
      <h1>Vehicle service</h1>
      <h3>Connection state: {connectionStatus}</h3>
      <h4>Speed: {speed} </h4>
    </div>
  );
};

export default VehicleApp;
