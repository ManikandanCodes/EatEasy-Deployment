import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { AnalyticsService } from '../../../core/services/analytics.service';

@Component({
  selector: 'app-analytics',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './analytics.component.html',
  styleUrls: ['./analytics.component.css']
})
export class AnalyticsComponent implements OnInit {

  stats: any = null;
  loading = true;
  error: string = '';

  constructor(private analyticsService: AnalyticsService) { }

  ngOnInit() {
    this.loadAnalytics();
  }

  loadAnalytics() {
    this.loading = true;
    this.error = '';

    this.analyticsService.getRestaurantAnalytics().subscribe({
      next: (res) => {
        this.stats = res;
        this.loading = false;
      },
      error: (err) => {
        console.error('Error loading analytics:', err);
        this.error = 'Failed to load analytics data';
        this.loading = false;
    
        this.stats = {
          totalOrders: 0,
          acceptedOrders: 0,
          rejectedOrders: 0,
          completedOrders: 0,
          totalRevenue: 0
        };
      }
    });
  }
}
