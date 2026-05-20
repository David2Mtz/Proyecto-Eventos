import { Routes } from '@angular/router';
import { HomeComponent } from './pages/home/home.component';
import { LoginComponent } from './pages/login/login.component';
import { DashboardDispatcherComponent } from './pages/dashboards/dispatcher/dashboard-dispatcher.component';
import { AdminDashboardComponent } from './pages/dashboards/admin/admin-dashboard.component';
import { StaffDashboardComponent } from './pages/dashboards/staff/staff-dashboard.component';
import { authGuard, roleGuard } from './guards/auth.guard';

export const routes: Routes = [
  { path: '', component: HomeComponent },
  { path: 'login', component: LoginComponent },
  { 
    path: 'inicio', 
    component: DashboardDispatcherComponent,
    canActivate: [authGuard]
  },
  { 
    path: 'admin', 
    component: AdminDashboardComponent,
    canActivate: [authGuard, roleGuard],
    data: { roles: ['ADMIN'] }
  },
  { 
    path: 'staff', 
    component: StaffDashboardComponent,
    canActivate: [authGuard, roleGuard],
    data: { roles: ['STAFF'] }
  },
  // We can add Anfitrion here too when implemented
  { path: '**', redirectTo: '' }
];
