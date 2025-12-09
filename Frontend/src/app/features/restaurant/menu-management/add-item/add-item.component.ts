import { Component, OnInit } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { MenuService } from '../../../../core/services/menu.service';
import { Router } from '@angular/router';
import { RestaurantService } from '../../../../core/services/restaurant.service';

@Component({
  selector: 'app-add-item',
  standalone: true,
  imports: [FormsModule, CommonModule],
  templateUrl: './add-item.component.html',
  styleUrls: ['./add-item.component.css']
})
export class AddItemComponent implements OnInit {

  categories: any[] = [];

  name = "";
  price: number = 0;
  description = "";
  categoryId: number | null = null;
  isVeg = false;
  imageUrl = "";
  error = "";

  constructor(
    private menuService: MenuService,
    private router: Router,
    private restaurantService: RestaurantService
  ) { }

  ngOnInit() {
    this.restaurantService.getMyRestaurant().subscribe(restaurant => {
      if (restaurant) {
        this.menuService.getCategories().subscribe(res => {
          this.categories = res;
        });
      }
    });
  }

  addItem() {
    if (!this.categoryId) {
      this.error = "Please select a category";
      return;
    }

    const data = {
      name: this.name,
      price: this.price,
      description: this.description,
      imageUrl: this.imageUrl,
      categoryId: this.categoryId,
      veg: this.isVeg
    };

    this.menuService.addItem(data).subscribe({
      next: () => this.router.navigate(['/restaurant/menu/items']),
      error: () => this.error = "Failed to add item"
    });
  }
}
