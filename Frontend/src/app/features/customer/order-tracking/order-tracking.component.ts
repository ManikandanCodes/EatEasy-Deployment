import { Component, OnInit, OnDestroy } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { CommonModule } from '@angular/common';
import { CustomerService } from '../../../core/services/customer.service';
import { interval, Subscription } from 'rxjs';

@Component({
  selector: 'app-order-tracking',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './order-tracking.component.html',
  styleUrls: ['./order-tracking.component.css']
})
export class OrderTrackingComponent implements OnInit, OnDestroy {

  order: any = null;
  loading = true;
  orderId!: number;
  poller!: Subscription;

  statuses = [
    "PLACED",
    "ACCEPTED",
    "PREPARING",
    "READY",
    "OUT_FOR_DELIVERY",
    "DELIVERED"
  ];

  constructor(private route: ActivatedRoute, private customerService: CustomerService) { }

  ngOnInit() {
    this.orderId = Number(this.route.snapshot.paramMap.get('orderId'));


    this.getOrder();

  
    this.poller = interval(5000).subscribe(() => {
      if (this.order?.status !== "DELIVERED") {
        this.getOrder();
      }
    });
  }

  getOrder() {
    this.customerService.getOrderTracking(this.orderId).subscribe({
      next: (res: any) => {
        this.order = res;
        this.loading = false;
      },
      error: (err) => {
        console.error("Error fetching order:", err);
        this.loading = false;
      }
    });
  }

  getStatusIndex(status: string) {
    return this.statuses.indexOf(status);
  }

  getStatusDescription(status: string): string {
    const descriptions: any = {
      "PLACED": "Your order has been placed and sent to the restaurant.",
      "ACCEPTED": "The restaurant has accepted your order.",
      "PREPARING": "Your food is being prepared in the kitchen.",
      "READY": "Your order is ready for pickup/delivery.",
      "OUT_FOR_DELIVERY": "Our delivery partner is on the way!",
      "DELIVERED": "Enjoy your meal!"
    };
    return descriptions[status] || "";
  }

  ngOnDestroy() {
    if (this.poller) this.poller.unsubscribe();
  }
}
