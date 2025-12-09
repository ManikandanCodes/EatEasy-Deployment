import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RestaurantService } from '../../../../core/services/restaurant.service';
import { Router } from '@angular/router';

@Component({
  selector: 'app-view-restaurant',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './view-restaurant.component.html',
  styleUrls: ['./view-restaurant.component.css']
})
export class ViewRestaurantComponent implements OnInit {

  restaurant: any = null;
  loading = true;

  constructor(private restService: RestaurantService, private router: Router) {}

  ngOnInit() {
    this.restService.getMyRestaurant().subscribe({
      next: (res) => {
        this.restaurant = res;
        this.loading = false;
      },
      error: () => {
        
        this.router.navigate(['/restaurant/add-restaurant']);
      }
    });
  }

  toggleStatus() {
    this.restService.updateStatus(this.restaurant.id, !this.restaurant.isOpen)
      .subscribe(() => this.restaurant.isOpen = !this.restaurant.isOpen);
  }

  editRestaurant() {
    this.router.navigate(['/restaurant/add-restaurant']);
  }
}
