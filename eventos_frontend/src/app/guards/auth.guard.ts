import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { AuthService } from '../services/auth.service';

export const authGuard: CanActivateFn = (route, state) => {
  const authService = inject(AuthService);
  const router = inject(Router);

  if (authService.isLoggedIn()) {
    return true;
  }

  // Redirect to login if not authenticated
  return router.parseUrl('/login');
};

export const roleGuard: CanActivateFn = (route, state) => {
  const authService = inject(AuthService);
  const router = inject(Router);
  const expectedRoles = route.data['roles'] as Array<string>;

  const userRoles = authService.currentUser()?.roles || [];
  const upperUserRoles = userRoles.map(r => r.toUpperCase());
  const upperExpectedRoles = expectedRoles.map(r => r.toUpperCase());

  const hasRole = upperExpectedRoles.some(role => upperUserRoles.includes(role));

  if (hasRole) {
    return true;
  }

  // Redirect to dispatcher if unauthorized for this specific route
  return router.parseUrl('/inicio');
};
