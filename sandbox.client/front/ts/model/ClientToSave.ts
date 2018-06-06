import {CharacterType} from "./CharacterType";
import {GenderType} from "./GenderType";
import {PhoneNumber} from "./PhoneNumber";
import {ClientAddress} from "./ClientAddress";

export class ClientToSave {
    public id: number /* int */;
    public name: string;
    public surname: string;
    public patronymic: string;
    public charm: number /* int */;
    public gender: GenderType;
    public reg: ClientAddress;
    public fact: ClientAddress;
    public birthDate: number /* long */;
    public age: number /* int */;
    public factAddress: string;
    public regAddress: string;
    public phones: Array<PhoneNumber>;

    public assign(o: any): ClientToSave {
        this.id = o.id;
        this.name = o.name;
        this.surname = o.surname;
        this.patronymic = o.patronymic;
        this.gender = o.gender;
        this.factAddress = o.factAddress;
        this.regAddress = o.regAddress;
        this.phones = o.phones;
        this.age = o.age;
        this.birthDate = o.birthDate;
        this.reg = o.reg;
        this.fact = o.fact;
        this.charm = o.charm;
        return this;
    }

    public static copy(json: any): ClientToSave {
        let result = new ClientToSave();
        result.assign(json);
        return result;
    }

}

