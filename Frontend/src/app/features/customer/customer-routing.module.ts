import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { authGuard } from '../../core/guards/auth.guard';

const routes: Routes = [


  {
    path: 'restaurants',
    loadComponent: () =>
      import('./restaurant-list/restaurant-list.component')
        .then(m => m.RestaurantListComponent)
  },


  {
    path: 'restaurant/:id',
    loadComponent: () =>
      import('./restaurant-detail/restaurant-detail.component')
        .then(m => m.RestaurantDetailComponent)
  },

  {
    path: 'cart',
    canActivate: [authGuard],
    loadComponent: () =>
      import('./cart/cart.component')
        .then(m => m.CartComponent)
  },

  {
    path: 'orders',
    canActivate: [authGuard],
    loadComponent: () =>
      import('./order-history/order-history.component')
        .then(m => m.OrderHistoryComponent)
  },

  {
    path: 'track/:orderId',
    canActivate: [authGuard],
    loadComponent: () =>
      import('./order-tracking/order-tracking.component')
        .then(m => m.OrderTrackingComponent)
  },

  {
    path: 'checkout',
    canActivate: [authGuard],
    loadComponent: () =>
      import('./checkout/checkout.component')
        .then(m => m.CheckoutComponent)
  },
  
  {
    path: 'home',
    loadComponent: () =>
      import('./home/home.component').then(m => m.HomeComponent)
  }
  ,




  
  { path: '', redirectTo: 'home', pathMatch: 'full' }

];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class CustomerRoutingModule { }
