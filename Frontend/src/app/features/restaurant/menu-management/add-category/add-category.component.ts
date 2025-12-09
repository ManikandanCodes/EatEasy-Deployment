import { Component, OnInit } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { MenuService } from '../../../../core/services/menu.service';
import { Router } from '@angular/router';
import { CommonModule } from '@angular/common';
import { RestaurantService } from '../../../../core/services/restaurant.service';

@Component({
  selector: 'app-add-category',
  standalone: true,
  imports: [FormsModule, CommonModule],
  templateUrl: './add-category.component.html',
  styleUrls: ['./add-category.component.css']
})
export class AddCategoryComponent implements OnInit {

  name = "";
  error = "";
  restaurantId: number | null = null;

  constructor(
    private menuService: MenuService,
    private router: Router,
    private restaurantService: RestaurantService
  ) { }

  ngOnInit() {
    this.restaurantService.getMyRestaurant().subscribe({
      next: (res: any) => {
        if (res) this.restaurantId = res.id;
      },
      error: () => this.error = "Could not fetch restaurant details"
    });
  }

  save() {
    if (!this.restaurantId) {
      this.error = "Restaurant not found";
      return;
    }

    this.menuService.addCategory({ name: this.name }).subscribe({
      next: () => this.router.navigate(['/owner/menu/categories']),
      error: () => this.error = "Failed to add category"
    });
  }
}
