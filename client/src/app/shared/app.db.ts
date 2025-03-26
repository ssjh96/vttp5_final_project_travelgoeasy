import Dexie, {Table} from 'dexie';
import { Country } from '../model/country';
import { Injectable } from '@angular/core';

// act injectable to make it a service
@Injectable({
    providedIn:"root"
})

export class AppDB extends Dexie 
{
    // Table<T, Key> -> value type, key type
    countries!: Table<Country, string>;

    constructor()
    {
        super("TravelGoEasy");
        this.version(1).stores({
            countries: 'code' // name is the PK (fist attribute)
        });
    }

    // COUNTRIES
    async addCountry(country: Country): Promise<void> // async makes function a promise
    {
        await this.countries.put(country); 
        // await makes the function pause the execution and wait for a resolved promise before it continues
    }

    // get country by pri key
    async getCountry(code: string): Promise<Country | undefined>
    {
        return await this.countries.get(code);

        // const cArray = await this.countries.where('name').equals(country).toArray()
        // const c = cArray[0]
        // console.log(c)
    }

}

export const db = new AppDB();