import {CharacterType} from "./CharacterType";
import {GenderType} from "./GenderType";
import {PhoneNumber} from "./PhoneNumber";

export class ClientToSave {
    public id: string;
    public name: string;
    public surname: string;
    public patronymic: string | null;
    public total: number /* int */;
    public max: number /* int */;
    public min: number /* int */;
    public character: CharacterType;
    public gender: GenderType;
    public age: number /* int */;
    public factAddress: string;
    public regAddress: string;
    public phoneNumbers: Array<PhoneNumber>;

    public assign(o: any): ClientToSave {
        this.id = o.id;
        this.name = o.name;
        this.surname = o.surname;
        this.patronymic = o.patronymic;
        this.total = o.total;
        this.max = o.max;
        this.min = o.min;
        this.character = o.character;
        this.gender = o.gender;
        this.factAddress = o.factAddress;
        this.regAddress = o.regAddress;
        this.phoneNumbers = o.phoneNumbers;
        this.age = o.age;
        return this;
    }

    public static copy(json: any): ClientToSave {
        let result = new ClientToSave();
        result.assign(json);
        return result;
    }

}

