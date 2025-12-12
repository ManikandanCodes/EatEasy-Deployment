import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { AdminService } from '../../../core/services/admin.service';

@Component({
  selector: 'app-coupon-management',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './coupon-management.component.html',
  styleUrls: ['./coupon-management.component.css']
})
export class CouponManagementComponent implements OnInit {

  coupons: any[] = [];
  loading = true;


  code = '';
  discount = 0;
  type = 'PERCENTAGE';
  expiry = '';
  minPurchaseAmount = 0;

  constructor(private adminService: AdminService) { }

  ngOnInit() {
    this.loadCoupons();
  }

  loadCoupons() {
    this.adminService.getCoupons().subscribe({
      next: (res: any) => {
        this.coupons = res as any[];
        this.loading = false;
      }
    });
  }


  addCoupon() {
    if (!this.code || !this.discount || !this.expiry) {
      alert('All fields are required');
      return;
    }

    const newCoupon = {
      code: this.code,
      discount: this.discount,
      type: this.type,
      expiryDate: this.expiry,
      minPurchaseAmount: this.minPurchaseAmount
    };

    this.adminService.addCoupon(newCoupon).subscribe({
      next: () => {
        alert('Coupon added successfully!');
        this.loadCoupons();
        this.code = '';
        this.discount = 0;
        this.type = 'PERCENTAGE';
        this.expiry = '';
        this.minPurchaseAmount = 0;
      },
      error: (err) => {
        console.error('Error adding coupon:', err);
        alert('Failed to add coupon. Please try again.');
      }
    });
  }

  deleteCoupon(id: number) {
    if (confirm('Are you sure you want to delete this coupon?')) {
      this.adminService.deleteCoupon(id).subscribe({
        next: () => {
          alert('Coupon deleted successfully');
          this.loadCoupons();
        },
        error: (err) => {
          console.error('Error deleting coupon:', err);
          alert('Failed to delete coupon');
        }
      });
    }
  }

  formatDate(dateString: string): string {
    if (!dateString) return 'N/A';
    const date = new Date(dateString);
    return date.toLocaleDateString('en-US', {
      year: 'numeric',
      month: 'short',
      day: 'numeric'
    });
  }

  isExpired(dateString: string): boolean {
    if (!dateString) return false;
    const expiryDate = new Date(dateString);
    const today = new Date();
    today.setHours(0, 0, 0, 0);
    return expiryDate < today;
  }
}
