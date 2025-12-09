import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, Router } from '@angular/router';
import { CustomerService } from '../../../core/services/customer.service';

@Component({
  selector: 'app-restaurant-list',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './restaurant-list.component.html',
  styleUrls: ['./restaurant-list.component.css']
})
export class RestaurantListComponent implements OnInit {

  restaurants: any[] = [];
  loading = true;

  constructor(
    private customerService: CustomerService,
    private router: Router,
    private route: ActivatedRoute
  ) { }

  ngOnInit() {
    this.route.queryParams.subscribe((params: any) => {

      const search = params['search'] || '';
      const location = params['location'] || '';
      const cuisine = params['cuisine'] || '';

      this.customerService.getRestaurants(search, location, cuisine)
        .subscribe({
          next: (res: any) => {
            this.restaurants = Array.isArray(res) ? res : [];
            this.loading = false;
          },
          error: (err) => {
            console.error('Error fetching restaurants:', err);
            this.loading = false;
          }
        });


    });
  }

  viewRestaurant(id: number) {
    this.router.navigate(['/customer/restaurant', id]);
  }
}
