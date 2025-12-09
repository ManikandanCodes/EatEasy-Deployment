import { Component, OnInit, HostListener } from '@angular/core';
import { RouterModule, Router } from '@angular/router';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { CustomerService } from '../../../core/services/customer.service';

@Component({
  selector: 'app-home',
  standalone: true,
  imports: [
    CommonModule,
    RouterModule
  ],
  templateUrl: './home.component.html',
  styleUrls: ['./home.component.css']
})
export class HomeComponent implements OnInit {

  location = '';
  searchText = '';
  isMobileView = false;

  cuisines = ["South Indian", "North Indian", "Chinese", "Biryani", "Fast Food"];

  topRestaurants: any[] = [];
  coupons: any[] = [];

  constructor(
    private router: Router,
    private customerService: CustomerService
  ) {
    this.checkViewportSize();
  }

  ngOnInit() {
    this.loadTopRestaurants();
    this.loadCoupons();
  }

  
  @HostListener('window:resize', ['$event'])
  onResize(event: any) {
    this.checkViewportSize();
  }

  private checkViewportSize() {
    this.isMobileView = window.innerWidth <= 768;
  }

  loadCoupons() {
    this.customerService.getActiveCoupons().subscribe({
      next: (res: any) => {
        this.coupons = res;
      },
      error: (err) => {
        console.error('Error loading coupons:', err);
      }
    });
  }

  loadTopRestaurants() {
    this.customerService.getRestaurants().subscribe({
      next: (res: any) => {

        console.log("Restaurants response:", res);

        
        if (!Array.isArray(res)) {
          console.error("Error: Response is not an array", res);
          this.topRestaurants = [];
          return;
        }

        this.topRestaurants = res.slice(0, 4).map(r => ({
          id: r?.id || null,
          name: r?.name || 'Unnamed Restaurant',
          cuisines: r?.cuisines || 'Unknown Cuisine',
          rating: r?.rating || 0,
          imageUrl: r?.imageUrl || 'assets/default-restaurant.jpg'
        }));

      },
      error: (err) => {
        console.error('Error loading top restaurants:', err);
        this.topRestaurants = [];
      }
    });
  }

  searchRestaurants() {
    this.router.navigate(['/customer/restaurants'], {
      queryParams: { search: this.searchText, location: this.location }
    });
  }

  selectCuisine(cuisine: string) {
    this.router.navigate(['/customer/restaurants'], {
      queryParams: { cuisine }
    });
  }

  copyCode(code: string) {
    navigator.clipboard.writeText(code);
    alert('Coupon code copied: ' + code);
  }
}
