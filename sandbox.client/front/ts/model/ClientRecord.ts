import {CharacterType} from "./CharacterType";
import {ClientToSave} from "./ClientToSave";

function getAge(birthDate: Date): number /* int */ {
    const today = new Date();
    let age = today.getFullYear() - birthDate.getFullYear();
    const m = today.getMonth() - birthDate.getMonth();
    if (m < 0 || (m === 0 && today.getDate() < birthDate.getDate())) {
        age--;
    }
    return age;
}

export class ClientRecord {
    public name: string;
    public surname: string;
    public patronymic: string;
    public total: number /* int */;
    public max: number /* int */;
    public min: number /* int */;
    public character: CharacterType;
    public age: number /* int */;

    public static copy(c: ClientToSave): ClientRecord {
        let result = new ClientRecord();
        result.name = c.name;
        result.surname = c.surname;
        result.patronymic = c.patronymic;
        result.total = c.total;
        result.max = c.max;
        result.min = c.min;
        result.character = c.character;
        result.age = c.age;
        return result;
    }

    public getFIO(): string {
        let result: string = '';
        if (this.name !== null && this.name.length !== 0) result += this.name;
        if (this.surname && this.name.length !== 0) result += this.surname;
        if (this.patronymic && this.name.length !== 0) result += this.patronymic;
        return result;
    }
}
