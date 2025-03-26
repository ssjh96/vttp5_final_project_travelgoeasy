import { Injectable } from "@angular/core";
import { ComponentStore } from "@ngrx/component-store";
import { Country } from "../model/country";
import { from, map, Observable, switchMap, tap } from "rxjs";
import { liveQuery } from "dexie";
import { db } from "../shared/app.db";

export interface CountriesState {
    countries: Country[];
    loading: boolean;
}

@Injectable({
    providedIn: 'root'
})

export class CountriesStore extends ComponentStore<CountriesState>{
    
    constructor() 
    {
        super( { countries:[], loading: false } ); // init empty array, loading false 
    }

    // Selector - read data from state, selectors are observables
    readonly countries$ = this.select(state => 
                            {
                                // DEBUG
                                // console.log("countries$: ", state.countries)

                                return state.countries.sort((a, b) => a.name.localeCompare(b.name)); // return countries array in alphabetical order
                            });

    readonly loading$ = this.select(state => state.loading);     
        

    // Updaters
    readonly setCountries = this.updater<Country[]>(
        (state, countriesData: Country[]) => 
        {
            // DEBUG
            // console.log("State before update: ", state);
            // console.log("Updating with countriesData: ", countriesData);

            return { ...state, countries: [...countriesData] } // return new state
        });

    readonly setLoading = this.updater((state, loading: boolean) => ({ ...state, loading }));

    // readonly setLoading2 = this.updater<boolean>(
    //     (state, loading: boolean) => 
    //     {
    //         return { ...state, loading }
    //     });
    

    // Effects
    // load countries from indexedDB
    readonly loadCountries = this.effect((trigger$: Observable<void>) =>
        trigger$.pipe(
            tap(() => this.setLoading(true)),
            switchMap(() => // switch to new obs -> liveQuery creates obs
                from(liveQuery(() => db.countries.reverse().toArray())).pipe(
                    tap({
                        next: (countries) => 
                        {
                            // DEBUG
                            // console.log("countries from indexedDB: ", countries)
                            this.setCountries(countries)
                        },
                        error: (error) => 
                        {
                            console.error('Error loading countries: ', error.message)
                            this.setLoading(false)
                        }
                    })
                )
            )
        )
    );

    // Add new country
    readonly addNewCountry = this.effect((countries$: Observable<Country>) =>
        countries$.pipe(
            switchMap((country) =>
                // placed into indexDB
                from(db.addCountry(country)).pipe(
                    tap(() => 
                    {
                        // DEBUG
                        // console.log("Loading Countries")

                        this.loadCountries()
                    })
                )
            )
        )
    );
    
}

