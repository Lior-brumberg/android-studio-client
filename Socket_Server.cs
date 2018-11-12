using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Net;
using System.Net.Sockets;
using System.Threading.Tasks;


/// <summary>
///A server class that builds initial connection, send connected message and then only listens for data
/// </summary>
namespace Server
{
    //Base class for Aid
    abstract class server_tools
    {
        protected TcpListener server;
        protected byte[] recieved;
        protected byte[] to_send;
        protected NetworkStream stream;

        public abstract void Run(string ip, int port);
    }
    //full implementation
    class Socket_Server : server_tools
    {
        private TcpClient End_client;

        public Socket_Server(TcpListener s)
        {
            this.server = s;
        }

        public override void Run(string ip, int port)
        {
            //Argument check
            if (ip == "" || port <= 0 || port >= 65536)
                throw new ArgumentException("Arguments given are not under the requierments.");
            else
            {
                if (this.server == null)
                    this.server = new TcpListener(IPAddress.Parse(ip), port);
                this.server.Start();

                while(true)
                {
                    Console.WriteLine("loop started...");
                    //get end point client
                    this.End_client = this.server.AcceptTcpClient();
                    Console.WriteLine("client connected...");
                    //get its data stream
                    this.stream = this.End_client.GetStream();

                    this.to_send = new byte[1024];
                    //base "connected" message
                    this.to_send = Encoding.Default.GetBytes("connected");

                    this.stream.Write(this.to_send, 0, this.to_send.Length);
                    
                    // data loop
                    while(this.End_client.Connected)
                    {
                        int size = stream.ReadByte();
                        Console.WriteLine(size);
                        
                        this.recieved = new byte[1024];
                        stream.Read(recieved, 0, size);
                       
                        Console.WriteLine(Encoding.Default.GetString(this.recieved));
                    }
                }
            }
        }
    }
}
