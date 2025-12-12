import { Component, OnInit, OnDestroy, ChangeDetectorRef, HostListener } from '@angular/core';
import { AuthService } from '../../../core/services/auth.service';
import { CartService } from '../../../core/services/cart.service';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { Subscription } from 'rxjs';

@Component({
  selector: 'app-navbar',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './navbar.component.html',
  styleUrls: ['./navbar.component.css']
})
export class NavbarComponent implements OnInit, OnDestroy {

  menuOpen = false;
  isLoggedIn = false;
  userRole: string | null = null;
  cartCount = 0;
  private authSub: Subscription | undefined;
  private cartSub: Subscription | undefined;

  constructor(
    public auth: AuthService,
    private cartService: CartService,
    private cdr: ChangeDetectorRef
  ) { }

  ngOnInit() {
    this.authSub = this.auth.loginStatus().subscribe(status => {
      console.log('Navbar: loginStatus received:', status);
      this.isLoggedIn = status;
      if (status) {
        const user = this.auth.getUser();
        this.userRole = user?.role || null;
      } else {
        this.userRole = null;
      }
      this.cdr.detectChanges();
    });

    this.cartSub = this.cartService.cart$.subscribe(cart => {
      this.cartCount = Object.values(cart).reduce((acc: number, item: any) => acc + item.quantity, 0);
      this.cdr.detectChanges();
    });
  }

  ngOnDestroy() {
    if (this.authSub) {
      this.authSub.unsubscribe();
    }
    if (this.cartSub) {
      this.cartSub.unsubscribe();
    }
  }


  @HostListener('window:resize', ['$event'])
  onResize(event: any) {
    const width = event.target.innerWidth;

    if (width > 768 && this.menuOpen) {
      this.menuOpen = false;
    }
  }

  toggleMenu() {
    this.menuOpen = !this.menuOpen;
  }

  logout() {
    this.auth.logout();
    this.menuOpen = false;
  }
}
