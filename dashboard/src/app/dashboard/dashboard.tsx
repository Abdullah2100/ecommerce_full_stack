"use client";

import NavLink from "@/components/layout/nav/navLinks";
import { useEffect, useRef, useState } from "react";
import useOrder from "@/stores/order";
import * as signalR from "@microsoft/signalr";
import { HubConnection } from "@microsoft/signalr";
import Main from "@/app/dashboard/body/main";

interface DashboardContentProps {}

const Dashboard = ({}: DashboardContentProps) => {
  const [selectedIndex, setSelectedIndex] = useState(0);
  const { isHasNewOrder, changeHasNewOrderStatus } = useOrder();
    const [connection, setConnection] = useState<signalR.HubConnection | undefined>(undefined);
  const audioRef = useRef<HTMLAudioElement>(null);

  const play = () => {
    if (audioRef.current) {
      audioRef.current.pause();
      audioRef.current.currentTime = 0;
      audioRef.current.play();
    }
  };

  useEffect(() => {
    const newConnection = new signalR.HubConnectionBuilder()
      .withUrl(`http://localhost:5077/orderHub`)
      .withAutomaticReconnect()
      .build();

    setConnection(newConnection);
  }, []);

  useEffect(() => {
    if (connection != undefined) {
      connection
        .start()
        .then(() => console.log("SignalR Connected!"))
        .catch((e) => console.log("Connection failed: ", e));

      connection.on("createdOrder", (user, message) => {
          console.log(`orders ${JSON.stringify(user)}`)
        changeHasNewOrderStatus(true);
        play();
      });

     }
  }, [connection]);

  useEffect(() => {
    console.log(`the selected index is ${selectedIndex}`);
  }, [selectedIndex]);

  return (
    <div className="flex flex-row px-10">
      <audio ref={audioRef} src="/sound/mixkit-bell-notification-933.wav" />
      <div className="w-auto ">
        <NavLink
          selectedIndex={selectedIndex}
          setSelectedIndex={setSelectedIndex}
          isNewOrder={isHasNewOrder}
        />
      </div>
      <div className="flex-grow overflow-y-auto">
        <Main currentPage={selectedIndex} />
      </div>
    </div>
  );
};

export default Dashboard;
