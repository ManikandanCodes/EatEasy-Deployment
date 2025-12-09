import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router, RouterModule } from '@angular/router';
import { RestaurantService } from '../../../../core/services/restaurant.service';

@Component({
  selector: 'app-add-restaurant',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './add-restaurant.component.html',
  styleUrls: ['./add-restaurant.component.css']
})
export class AddRestaurantComponent {

  name = "";
  address = "";
  cuisine = "";
  phone = "";
  error = "";

  constructor(
    private restService: RestaurantService,
    private router: Router
  ) { }

  updateName(event: any) { this.name = event.target.value; }
  updateAddress(event: any) { this.address = event.target.value; }
  updateCuisine(event: any) { this.cuisine = event.target.value; }
  updatePhone(event: any) { this.phone = event.target.value; }

  submitForm() {
    if (!this.name || !this.address || !this.cuisine || !this.phone) {
      this.error = "Please fill all required fields";
      return;
    }


    const userStr = localStorage.getItem('user');
    if (!userStr) {
      this.error = "User not found. Please login again.";
      return;
    }

    const user = JSON.parse(userStr);

    const data = {
      name: this.name,
      address: this.address,
      cuisines: this.cuisine,
      phone: this.phone,
      ownerId: user.id,
      rating: 0,
      openingHours: "9 AM - 10 PM",
      open: true
    };

    console.log("Creating restaurant with data:", data);

    this.restService.createRestaurant(data).subscribe({
      next: (response) => {
        console.log("Restaurant created successfully:", response);
        this.router.navigate(['/owner/dashboard']);
      },
      error: (err) => {
        console.error("Restaurant creation error:", err);
        this.error = err.error?.message || "Failed to create restaurant";
      }
    });
  }
}
