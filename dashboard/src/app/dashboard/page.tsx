'use client';

import dynamic from 'next/dynamic';
import withAuth from './DashboardWrapper';
import { ReactNode } from 'react';

const DashboardContent = dynamic(() => import('./DashboardContent'), { ssr: false });

interface DashboardPageProps {
    children?: ReactNode;
}

const DashboardPage = ({}: DashboardPageProps) => {
    return <DashboardContent />;
};

export default withAuth(DashboardPage);