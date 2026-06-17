import { nanoid } from 'nanoid';
class Script{
    constructor(question, answer) {
        this.id = nanoid();
        this.question = question;
        this.answer = answer;
      }
}

export default Script;