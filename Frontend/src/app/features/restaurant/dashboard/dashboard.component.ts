import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { RestaurantService } from '../../../core/services/restaurant.service';
import { OrderService } from '../../../core/services/order.service';
import { MenuService } from '../../../core/services/menu.service';

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './dashboard.component.html',
  styleUrls: ['./dashboard.component.css']
})
export class DashboardComponent implements OnInit {
  restaurant: any = null;
  loading = true;
  error: string = '';


  stats = {
    todayOrders: 0,
    totalRevenue: 0,
    menuItems: 0,
    avgRating: 0
  };

  constructor(
    private restService: RestaurantService,
    private orderService: OrderService,
    private menuService: MenuService,
    private router: Router
  ) { }

  ngOnInit(): void {
    this.loadRestaurantData();
  }

  loadRestaurantData(): void {
    this.loading = true;
    this.error = '';

    this.restService.getMyRestaurant().subscribe({
      next: (res: any) => {
        console.log('Restaurant data received:', res);
        this.restaurant = res ?? null;
        if (this.restaurant) {
          this.loadStatistics();
        }
        this.loading = false;
      },
      error: (err: any) => {
        console.error('Error fetching restaurant:', err);
        this.error = 'Failed to load restaurant data. Please refresh the page.';
        this.loading = false;
      }
    });
  }

  loadStatistics(): void {
    if (this.restaurant?.id) {
      this.menuService.getCategories(this.restaurant.id).subscribe({
        next: (categories: any[]) => {
          this.stats.menuItems = categories.reduce((total, cat) => total + (cat.items?.length || 0), 0);
        },
        error: (err) => console.error('Error loading menu stats:', err)
      });

      this.orderService.getRestaurantOrders().subscribe({
        next: (orders: any[]) => {
          const today = new Date().toDateString();
          const todayOrders = orders.filter(order => {
            if (!order.orderTime) return false;
            const orderDate = new Date(order.orderTime).toDateString();
            return orderDate === today;
          });

          this.stats.todayOrders = todayOrders.length;

          this.stats.totalRevenue = todayOrders
            .reduce((sum, order) => sum + (order.totalPrice || 0), 0);

          if (this.restaurant?.rating) {
            this.stats.avgRating = this.restaurant.rating;
          }
        },
        error: (err) => console.error('Error loading order stats:', err)
      });
    }
  }

  toggleStatus(): void {
    if (!this.restaurant) return;

    this.restService.updateStatus(this.restaurant.id, !this.restaurant.open).subscribe({
      next: () => {
        this.restaurant.open = !this.restaurant.open;
      },
      error: (err) => {
        console.error('Error updating status:', err);
        alert('Failed to update restaurant status');
      }
    });
  }

  editRestaurant(): void {
    this.router.navigate(['/owner/edit-restaurant']);
  }

  manageMenu(): void {
    this.router.navigate(['/owner/menu']);
  }

  viewOrders(): void {
    this.router.navigate(['/owner/orders']);
  }

  viewAnalytics(): void {
    this.router.navigate(['/owner/analytics']);
  }
}
