import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { AdminService } from '../../../core/services/admin.service';

@Component({
  selector: 'app-restaurant-management',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './restaurant-management.component.html',
  styleUrls: ['./restaurant-management.component.css']
})
export class RestaurantManagementComponent implements OnInit {

  restaurants: any[] = [];
  filteredRestaurants: any[] = [];
  loading = true;
  searchTerm: string = '';
  filterStatus: string = 'ALL';
  selectedRestaurant: any = null;

  constructor(private adminService: AdminService) { }

  ngOnInit() {
    this.loadRestaurants();
  }

  loadRestaurants() {
    this.adminService.getAllRestaurants().subscribe({
      next: (data) => {
        this.restaurants = (data as any).restaurants || (data as any[]) || [];
        this.filteredRestaurants = [...this.restaurants];
        this.loading = false;
        this.filterRestaurants();
      },
      error: (err) => {
        console.error('Error loading restaurants:', err);
        this.loading = false;
      }
    });
  }

  setFilter(status: string) {
    this.filterStatus = status;
    this.filterRestaurants();
  }

  filterRestaurants() {
    let result = [...this.restaurants];


    if (this.filterStatus !== 'ALL') {
      result = result.filter(r => r.status === this.filterStatus);
    }

   
    if (this.searchTerm) {
      const term = this.searchTerm.toLowerCase();
      result = result.filter(r =>
        r.name?.toLowerCase().includes(term) ||
        r.email?.toLowerCase().includes(term) ||
        r.ownerName?.toLowerCase().includes(term) ||
        r.address?.toLowerCase().includes(term)
      );
    }

    this.filteredRestaurants = result;
  }

  getPendingCount(): number {
    return this.restaurants.filter(r => r.status === 'PENDING').length;
  }

  getApprovedCount(): number {
    return this.restaurants.filter(r => r.status === 'APPROVED').length;
  }

  approveRestaurant(id: number) {
    this.adminService.approveRestaurant(id).subscribe(() => {
      this.loadRestaurants();
    });
  }

  blockRestaurant(id: number) {
    this.adminService.blockRestaurant(id).subscribe(() => {
      this.loadRestaurants();
    });
  }

  activateRestaurant(id: number) {
    this.adminService.activateRestaurant(id).subscribe(() => {
      this.loadRestaurants();
    });
  }

  viewDetails(restaurant: any) {
    this.selectedRestaurant = restaurant;
  }

  closeDetails() {
    this.selectedRestaurant = null;
  }
}
