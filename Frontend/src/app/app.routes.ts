import { Routes } from '@angular/router';

export const routes: Routes = [


  {
    path: 'login',
    loadComponent: () => import('./features/auth/login/login.component').then(m => m.LoginComponent)
  },
  {
    path: 'register',
    loadComponent: () => import('./features/auth/register/register.component').then(m => m.RegisterComponent)
  },
  {
    path: 'forgot-password',
    loadComponent: () => import('./features/auth/forgot-password/forgot-password.component').then(m => m.ForgotPasswordComponent)
  },

  {
    path: 'admin',
    loadChildren: () =>
      import('./features/admin/admin-routing.module')
        .then(m => m.AdminRoutingModule)
  },


  {
    path: 'owner',
    loadChildren: () =>
      import('./features/restaurant/restaurant-routing.module')
        .then(m => m.RestaurantRoutingModule)
  },




  {
    path: 'customer',
    loadChildren: () =>
      import('./features/customer/customer-routing.module')
        .then(m => m.CustomerRoutingModule)
  },


  { path: '', redirectTo: 'customer', pathMatch: 'full' },


  { path: '**', redirectTo: 'customer' }
];
