import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { CommonModule } from '@angular/common';
import { CustomerService } from '../../../core/services/customer.service';
import { CartService } from '../../../core/services/cart.service';


@Component({
  selector: 'app-restaurant-detail',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './restaurant-detail.component.html',
  styleUrls: ['./restaurant-detail.component.css']
})
export class RestaurantDetailComponent implements OnInit {

  restaurant: any = null;
  menu: any[] = [];
  loading = true;
  restaurantId!: number;

  cart: { [itemId: number]: number } = {}; 

  constructor(
    private route: ActivatedRoute,
    private customerService: CustomerService,
    private cartService: CartService
  ) { }

  ngOnInit() {
    this.restaurantId = Number(this.route.snapshot.paramMap.get('id'));
    this.loadRestaurant();
    this.updateLocalCart();
  }

  loadRestaurant() {
    this.customerService.getRestaurantDetail(this.restaurantId).subscribe({
      next: (res: any) => {
        this.restaurant = res;
        this.menu = [];

        if (this.restaurant.categories) {
          this.restaurant.categories.forEach((cat: any) => {
            if (cat.items) {
              this.menu.push(...cat.items);
            }
          });
        }

        this.loading = false;
      },
      error: (err) => {
        console.error('Error loading restaurant:', err);
        this.loading = false;
      }
    });
  }

  addToCart(item: any) {
    const currentItems: any[] = this.cartService.getCartItems();

    if (currentItems.length > 0 && currentItems[0].restaurantId !== this.restaurantId) {
      if (confirm('Your cart contains items from another restaurant. Reset cart for this restaurant?')) {
        this.cartService.clearCart();
      } else {
        return;
      }
    }

    const itemWithRestaurant = { ...item, restaurantId: this.restaurantId };
    this.cartService.addItem(itemWithRestaurant);
    this.updateLocalCart();
  }

  increase(item: any) {
    this.cartService.increaseQuantity(item.id);
    this.updateLocalCart();
  }

  decrease(item: any) {
    this.cartService.decreaseQuantity(item.id);
    this.updateLocalCart();
  }

  updateLocalCart() {
    this.cart = this.cartService.getCartQuantities();
  }
}
