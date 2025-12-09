import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router, RouterModule } from '@angular/router';
import { RestaurantService } from '../../../../core/services/restaurant.service';

@Component({
    selector: 'app-edit-restaurant',
    standalone: true,
    imports: [CommonModule, RouterModule],
    templateUrl: './edit-restaurant.component.html',
    styleUrls: ['./edit-restaurant.component.css']
})
export class EditRestaurantComponent implements OnInit {
    restaurant: any = {
        name: '',
        address: '',
        cuisines: '',
        phone: '',
        description: '',
        imageUrl: '',
        isOpen: false
    };
    loading = true;
    errorMsg = '';

    constructor(private restService: RestaurantService, private router: Router) { }

    ngOnInit(): void {
        this.restService.getMyRestaurant().subscribe({
            next: (res: any) => {
                if (res) {
                    this.restaurant = res;
                }
                this.loading = false;
            },
            error: (err) => {
                console.error('Error loading restaurant for edit:', err);
                this.errorMsg = 'Unable to load restaurant details.';
                this.loading = false;
            }
        });
    }


    updateName(event: any) { this.restaurant.name = event.target.value; }
    updateAddress(event: any) { this.restaurant.address = event.target.value; }
    updateCuisines(event: any) { this.restaurant.cuisines = event.target.value; }
    updatePhone(event: any) { this.restaurant.phone = event.target.value; }
    updateDescription(event: any) { this.restaurant.description = event.target.value; }
    updateImageUrl(event: any) { this.restaurant.imageUrl = event.target.value; }
    updateIsOpen(event: any) { this.restaurant.isOpen = event.target.checked; }

    saveChanges() {
        if (!this.restaurant?.id) {
            this.errorMsg = 'Restaurant ID missing.';
            return;
        }
        this.restService.updateRestaurant(this.restaurant.id, this.restaurant).subscribe({
            next: () => {
                this.router.navigate(['/owner/dashboard']);
            },
            error: (err) => {
                console.error('Error saving restaurant:', err);
                this.errorMsg = 'Failed to save changes.';
            }
        });
    }

    cancel() {
        this.router.navigate(['/owner/dashboard']);
    }
}
