package us.blav.hd.reca;

import java.util.ArrayList;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import us.blav.hd.BinaryVector;
import us.blav.hd.Hyperspace;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class TransitionTest {

    @Test
    public void should_init_transition() {
        Hyperspace hyperspace = new Hyperspace(16);
        Transition transition = new Transition(new Rule(90));
        BinaryVector vector = hyperspace.newZero();
        vector.bits().set(0);
        ArrayList<String> result = new ArrayList<>();
        for (int i = 0; i < 32; i++) {
            result.add(vector.toString().replace('0', ' '));
            vector = transition.next(vector);
        }

        assertThat(result).containsExactly(
                "1               ",
                " 1              ",
                "1 1             ",
                "   1            ",
                "  1 1           ",
                " 1   1          ",
                "1 1 1 1         ",
                "       1        ",
                "      1 1       ",
                "     1   1      ",
                "    1 1 1 1     ",
                "   1       1    ",
                "  1 1     1 1   ",
                " 1   1   1   1  ",
                "1 1 1 1 1 1 1 1 ",
                "               1",
                "              11",
                "             11 ",
                "            1111",
                "           11   ",
                "          1111  ",
                "         11  11 ",
                "        11111111",
                "       11       ",
                "      1111      ",
                "     11  11     ",
                "    11111111    ",
                "   11      11   ",
                "  1111    1111  ",
                " 11  11  11  11 ",
                "1111111111111111",
                "1               "
        );
    }
}