import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

const routes: Routes = [
  {
    path: '',
    children: [
      {
        path: 'dashboard',
        loadComponent: () =>
          import('./dashboard/dashboard.component')
            .then(m => m.DashboardComponent)
      },

      {
        path: 'add-restaurant',
        loadComponent: () =>
          import('./restaurant-profile/add-restaurant/add-restaurant.component')
            .then(m => m.AddRestaurantComponent)
      },

      {
        path: 'edit-restaurant',
        loadComponent: () =>
          import('./restaurant-profile/edit-restaurant/edit-restaurant.component')
            .then(m => m.EditRestaurantComponent)
      },

      {
        path: 'profile',
        loadComponent: () =>
          import('./restaurant-profile/view-restaurant/view-restaurant.component')
            .then(m => m.ViewRestaurantComponent)
      },

 
      {
        path: 'menu',
        children: [
          {
            path: '',
            loadComponent: () =>
              import('./menu-management/menu-management.component')
                .then(m => m.MenuManagementComponent)
          },

          {
            path: 'categories',
            loadComponent: () =>
              import('./menu-management/category-list/category-list.component')
                .then(m => m.CategoryListComponent)
          },

          {
            path: 'add-category',
            loadComponent: () =>
              import('./menu-management/add-category/add-category.component')
                .then(m => m.AddCategoryComponent)
          },

          {
            path: 'items',
            loadComponent: () =>
              import('./menu-management/item-list/item-list.component')
                .then(m => m.ItemListComponent)
          },

          {
            path: 'add-item',
            loadComponent: () =>
              import('./menu-management/add-item/add-item.component')
                .then(m => m.AddItemComponent)
          },

          { path: '', redirectTo: '', pathMatch: 'full' }
        ]
      },

  
      {
        path: 'orders',
        loadComponent: () =>
          import('./orders/orders.component')
            .then(m => m.OrdersComponent)
      },


      {
        path: 'analytics',
        loadComponent: () =>
          import('./analytics/analytics.component')
            .then(m => m.AnalyticsComponent)
      },

 
      { path: '', redirectTo: 'dashboard', pathMatch: 'full' }
    ]
  }
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class RestaurantRoutingModule { }
