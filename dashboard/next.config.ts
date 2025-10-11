import type { NextConfig } from "next";

const nextConfig: NextConfig = {
  /* config options here */
  eslint: {
    ignoreDuringBuilds: true,
  },
  images: {
    domains: ['localhost'],
  },
    allowedDevOrigins: ['192.168.1.45', 'localhost'],
};

export default nextConfig;
