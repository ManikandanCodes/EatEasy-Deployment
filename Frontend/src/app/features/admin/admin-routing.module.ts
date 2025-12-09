import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

const routes: Routes = [
  {
    path: '',
    children: [
      {
        path: 'dashboard',
        loadComponent: () =>
          import('./admin-dashboard/admin-dashboard.component')
            .then(m => m.AdminDashboardComponent)
      },

      {
        path: 'analytics',
        loadComponent: () =>
          import('./analytics/analytics.component')
            .then(m => m.AnalyticsComponent)
      },

      {
        path: 'restaurants',
        loadComponent: () =>
          import('./restaurant-management/restaurant-management.component')
            .then(m => m.RestaurantManagementComponent)
      },

      {
        path: 'users',
        loadComponent: () =>
          import('./user-management/user-management.component')
            .then(m => m.UserManagementComponent)
      },

      {
        path: 'coupons',
        loadComponent: () =>
          import('./coupon-management/coupon-management.component')
            .then(m => m.CouponManagementComponent)
      },

      { path: '', redirectTo: 'dashboard', pathMatch: 'full' }
    ]
  }
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class AdminRoutingModule {}
