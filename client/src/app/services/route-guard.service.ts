import { inject, Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, CanActivate, GuardResult, MaybeAsync, Router, RouterStateSnapshot } from '@angular/router';
import { AuthenticationService } from './authentication.service';

@Injectable({
  providedIn: 'root'
})
export class RouteGuardService implements CanActivate{

  private router: Router = inject(Router);
  private authSvc: AuthenticationService = inject(AuthenticationService);

  constructor() { }

  canActivate(route: ActivatedRouteSnapshot, state: RouterStateSnapshot): MaybeAsync<GuardResult> {

    const isAuth =  this.authSvc.isAuthenticated();
    console.log("routeGuard - isAuth: ", isAuth)
    
    if(isAuth)
    {
      return true;
    }
    console.log("RouteGuardSvc - failure")
    alert("Please login..")
    return this.router.parseUrl("/Login");
  }


}